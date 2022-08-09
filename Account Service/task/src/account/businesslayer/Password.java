package account.businesslayer;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class Password {
    @NotEmpty
    @Size(min = 12, message = "Password length must be 12 chars minimum!")
    @JsonProperty("new_password")
    private String passValue;

    public Password() {
    }

    public Password(String passValue) {
        this.passValue = passValue;
    }

    public String getPassValue() {
        return passValue;
    }

    public void setPassValue(String passValue) {
        this.passValue = passValue;
    }
}
