package com.example.poyangreportbackend.service.visitor;

public interface VisitorService {

    public boolean sendCode(String phone);
    public boolean verifyCode(String phone, String code);
}
