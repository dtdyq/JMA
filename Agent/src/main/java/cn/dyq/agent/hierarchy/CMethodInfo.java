package cn.dyq.agent.hierarchy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CMethodInfo {
    public String cClassName;// java/lang/String
    public String methodName;
    public String descriptor;// (II)I
}
