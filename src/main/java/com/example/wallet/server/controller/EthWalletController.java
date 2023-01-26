package com.example.wallet.server.controller;

import com.example.wallet.server.service.WalletService;
import lombok.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/wallet")
public class EthWalletController {
    private final WalletService walletService;

    public EthWalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    @ResponseBody
    String createWallet(@NonNull String password) {
        return walletService.generateWallet(password);
    }
}
