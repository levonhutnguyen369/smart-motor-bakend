package backend.datn.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimBalanceResponse {
    private String phone;

    private String operator;

    @JsonProperty("package")
    private String packageName;

    private Long balance;

    private String expireDate;
}
