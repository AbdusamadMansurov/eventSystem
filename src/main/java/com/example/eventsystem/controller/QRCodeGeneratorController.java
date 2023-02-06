package com.example.eventsystem.controller;

import com.example.eventsystem.service.QRCodeGeneratorService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.UUID;

/**
 * @author Malikov Azizjon  *  13.01.2023  *  20:36   *  ourSystem
 */
@RestController
@RequestMapping("/qr")
public class QRCodeGeneratorController {

    @GetMapping("/qrcode")
    public String getQRCode(Model model){

        byte[] image = new byte[0];

        // Generate and Return Qr Code in Byte Array
        image = QRCodeGeneratorService.getQRCodeImage(UUID.randomUUID().toString(),250,250);

        // Generate and Save Qr Code Image in static/image folder
//        QRCodeGenerator.generateQRCodeImage(github,250,250,QR_CODE_IMAGE_PATH);

        // Convert Byte Array into Base64 Encode String
        String qrcode = Base64.getEncoder().encodeToString(image);

        model.addAttribute("qrcode",qrcode);

        return "qrcode";
    }

}
