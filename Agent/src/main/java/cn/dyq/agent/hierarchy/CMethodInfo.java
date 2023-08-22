package cn.dyq.agent.hierarchy;

import java.util.Objects;

public class CMethodInfo {
    public String cClassName;// java/lang/String
    public String methodName;

    public CMethodInfo(String cClassName, String methodName) {
        this.cClassName = cClassName;
        this.methodName = methodName;
    }

    public CMethodInfo() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CMethodInfo that = (CMethodInfo) o;
        return cClassName.equals(that.cClassName) && methodName.equals(that.methodName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cClassName, methodName);
    }

    @Override
    public String toString() {
        return "CMethodInfo{" +
                "cClassName='" + cClassName + '\'' +
                ", methodName='" + methodName + '\'' +
                '}';
    }
}
