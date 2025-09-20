package github.openautonomousconnection.protocol.versions.v1_0_0.beta;

import lombok.Getter;

import java.io.Serializable;

public enum DNSResponseCode implements Serializable {
    RESPONSE_NOT_REQUIRED(0, "Response code not required"),
    RESPONSE_INVALID_REQUEST(1, "Invalid request"),

    RESPONSE_AUTH_SUCCESS(4, "Auth success"),
    RESPONSE_AUTH_FAILED(5, "Auth failed"),

    RESPONSE_DOMAIN_NAME_EXIST(100, "Domainname exist"),
    RESPONSE_DOMAIN_NAME_NOT_EXIST(101, "Domainname does not exist"),
    RESPONSE_DOMAIN_NAME_CREATED(105, "Domainname created"),
    RESPONSE_DOMAIN_NAME_DELETED(106, "Domainname deleted"),

    RESPONSE_DOMAIN_TLN_EXIST(110, "TopLevelName exist"),
    RESPONSE_DOMAIN_TLN_NOT_EXIST(111, "TopLevelName does not exist"),
    RESPONSE_DOMAIN_TLN_CREATED(115, "TopLevelName created"),
    RESPONSE_DOMAIN_TLN_DELETED(116, "TopLevelName deleted"),

    RESPONSE_DOMAIN_SUBNAME_EXIST(120, "Subname exist"),
    RESPONSE_DOMAIN_SUBNAME_NOT_EXIST(121, "Subname does not exist"),
    RESPONSE_DOMAIN_SUBNAME_CREATED(125, "Subname created"),
    RESPONSE_DOMAIN_SUBNAME_DELETED(126, "Subname deleted"),

    RESPONSE_DOMAIN_FULLY_EXIST(130, "Full domain exist"),
    RESPONSE_DOMAIN_FULLY_NOT_EXIST(131, "Full domain does not exist")
    ;

    @Getter
    private final int code;

    @Getter
    private final String description;

    DNSResponseCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String toString() {
        return "{code=" + code + ";description=" + description +"}";
    }
}
