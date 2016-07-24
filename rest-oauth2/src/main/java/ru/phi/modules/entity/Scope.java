package ru.phi.modules.entity;

public enum Scope {
    userRead("read:user"),
    userWrite("write:user"),
    userDelete("delete:user"),
    profileWrite("write:profile"),
    profileRead("read:profile"),
    settingsWrite("write:settings"),
    settingsRead("read:settings");

    Scope(String scope) {
        this.scope = scope;
    }
    private final String scope;

    public static String preAuthorize(UserRole role, Scope ... scopes) {
        final StringBuilder builder = new StringBuilder();
        builder.append("hasRole('").append(role.getAuthority()).append("')");
        for (Scope scope: scopes) {
            builder.append(" and #oauth2.hasScope('").append(scope.scope).append("')");
        }
        return builder.toString();
    }
}
