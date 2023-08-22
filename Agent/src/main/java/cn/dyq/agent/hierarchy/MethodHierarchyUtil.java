package cn.dyq.agent.hierarchy;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

public class MethodHierarchyUtil {
    public static void main(String[] args) throws IOException {

        readClassMethodHierarchy("cn/dyq/agent/ClassA", "runA").forEach(System.out::println);
    }

    public static Set<CMethodInfo> readClassMethodHierarchy(String root, String name) throws IOException {
        // 读取字节码文件
        Set<CMethodInfo> methods = new LinkedHashSet<>();
        ClassReader classReader = new ClassReader(root);

        HierarchyClassVisitor specifyClassVisitor = new HierarchyClassVisitor(Opcodes.ASM8, root, name, methods);

        // 解析字节码
        classReader.accept(specifyClassVisitor, 0);

        //  methods.add(cMethodInfo);
        return methods;
    }

}
