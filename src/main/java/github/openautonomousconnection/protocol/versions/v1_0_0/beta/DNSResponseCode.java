package github.openautonomousconnection.protocol.versions.v1_0_0.beta;

import lombok.Getter;

public enum DNSResponseCode {
    RESPONSE_NOT_REQUIRED(0, "Response code not required"),

    RESPONSE_AUTH_SUCCESS(1, "Auth success"),
    RESPONSE_AUTH_FAILED(2, "Auth failed"),

    RESPONSE_DOMAIN_EXIST(100, "Domain exist"),
    RESPONSE_DOMAIN_NOT_EXIST(101, "Domain does not exist"),
    RESPONSE_DOMAIN_CREATED(105, "Domain created"),
    RESPONSE_DOMAIN_DELETED(106, "Domain deleted"),

    RESPONSE_DOMAIN_TLN_EXIST(110, "TopLevelName exist"),
    RESPONSE_DOMAIN_TLN_NOT_EXIST(111, "TopLevelName does not exist"),
    RESPONSE_DOMAIN_TLN_CREATED(115, "TopLevelName created"),
    RESPONSE_DOMAIN_TLN_DELETED(116, "TopLevelName deleted"),

    RESPONSE_DOMAIN_SUBNAME_EXIST(120, "Subname exist"),
    RESPONSE_DOMAIN_SUBNAME_NOT_EXIST(121, "Subname does not exist"),
    RESPONSE_DOMAIN_SUBNAME_CREATED(125, "Subname created"),
    RESPONSE_DOMAIN_SUBNAME_DELETED(126, "Subname deleted"),
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
