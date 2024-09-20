package com.br.api.wifi_marketing.models.enums;

public enum StatusRole {

    ADMIN(1L),
    BASIC(2L);

    long roleId;

    StatusRole(long roleId) {
        this.roleId = roleId;
    }

    public long getRoleId() {
        return roleId;
    }

}
