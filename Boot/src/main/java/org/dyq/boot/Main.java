package org.dyq.boot;

import cn.hutool.json.JSONUtil;
import com.sun.tools.attach.VirtualMachine;
import org.dyq.boot.http.HttpSvrUtil;
import org.dyq.common.AgentParams;
import org.dyq.common.Settings;
import org.fusesource.jansi.AnsiConsole;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "jma", mixinStandardHelpOptions = true, version = "1.0")
public class Main implements Callable<Void> {
    @CommandLine.Option(names = {"-j", "--jvm"}, description = "target jvm process id", required = true)
    private int processId;
    @CommandLine.Option(names = {"-c", "--classname"}, description = "target class path", required = true)
    private String className;
    @CommandLine.Option(names = {"-m", "--method"}, description = "target method name", required = true)
    private String methodName;
    @CommandLine.Option(names = {"-p", "--port"}, description = "http trans port")
    private int port = Settings.HTTP_PORT_DEFAULT;

    public static void main(String[] args) throws IOException {
        AnsiConsole.systemInstall(); // enable colors on Windows
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Void call() {
        try {
            HttpSvrUtil.startSvr(port);
            URL url = Main.class.getProtectionDomain().getCodeSource().getLocation();
            // 将URL转换为File
            File file = new File(url.toURI());
            String agentJarPath = file.getCanonicalPath();
            VirtualMachine virtualMachine = VirtualMachine.attach(String.valueOf(processId));
            AgentParams params = new AgentParams(port, methodName, className);
            String arg = JSONUtil.toJsonStr(params);
            virtualMachine.loadAgent(agentJarPath, arg);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                AnsiConsole.systemUninstall(); // enable colors on Windows
                try {
                    virtualMachine.detach();
                } catch (IOException e) {
                    //
                }
            }));
            while (true) {
                Thread.sleep(100);
            }
        } catch (Throwable e) {
            PrintUtil.errorLine(e.getMessage());
        }
        return null;
    }

    @Override
    public String toString() {
        return "Main{" + "processId=" + processId + ", className='" + className + '\'' + ", methodName='" + methodName + '\'' + '}';
    }
}