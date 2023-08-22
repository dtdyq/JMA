package cn.dyq.agent.hierarchy;

import cn.dyq.agent.advice.AdviceUtil;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

class HierarchyClassVisitor extends ClassVisitor {

    private final String cName;
    private final String mName;
    private final Set<CMethodInfo> methods;

    protected HierarchyClassVisitor(int api, String cName, String mName, Set<CMethodInfo> methods) {
        super(api);
        this.cName = cName;
        this.mName = mName;
        this.methods = methods;
        String cc = cName.replaceAll("/", ".");
        try {
            Class<?> cClass = Class.forName(cc);
            List<Class<?>> supers = AdviceUtil.getAllSuperClasses(cClass);
            supers.forEach(new Consumer<Class<?>>() {
                @Override
                public void accept(Class<?> aClass) {
                    for (Method method : aClass.getDeclaredMethods()) {
                        methods.add(new CMethodInfo(aClass.getName().replaceAll("\\.", "/"), method.getName()));
                    }
                }
            });
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {

        CMethodInfo cMethodInfo = new CMethodInfo(cName, name);
        if (methods.contains(cMethodInfo)) {
            return null;
        }
        methods.add(cMethodInfo);
        if (!name.equals(mName)) {
            return null;
        }
        return new MethodVisitor(Opcodes.ASM8) {
            @Override
            public void visitMethodInsn(int opcode, String owner, String name1, String descriptor1, boolean isInterface) {
                // 打印方法调用信息
                if (!owner.equalsIgnoreCase(cName)) {
                    try {
                        ClassReader classReader1 = new ClassReader(owner);
                        HierarchyClassVisitor specifyClassVisitor = new HierarchyClassVisitor(Opcodes.ASM8, owner, name1, methods);
                        classReader1.accept(specifyClassVisitor, 0);
                        methods.add(new CMethodInfo(owner, name1));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
    }
}
