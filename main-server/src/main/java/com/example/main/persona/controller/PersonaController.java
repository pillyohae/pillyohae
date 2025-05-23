package com.example.main.persona.controller;


import com.example.main.persona.dto.PersonaMessageCreateRequestDto;
import com.example.main.persona.dto.PersonaMessageCreateResponseDto;
import com.example.main.persona.service.PersonaService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.image.Image;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/persona")
@RequiredArgsConstructor
public class PersonaController {

    private final PersonaService personaService;

    @PostMapping("/image")
    public ResponseEntity<Image> generatePersonaImage(@RequestParam("imageUrl") String productImageUrl) {
        try {
            var personaImageUrl = personaService.generatePersonaFromProduct(productImageUrl);
            return ResponseEntity.ok(personaImageUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/message")
    public ResponseEntity<List<PersonaMessageCreateResponseDto>> generatePersonaMessage(@Valid @RequestBody PersonaMessageCreateRequestDto requestDto) {
        try {
            return ResponseEntity.ok(personaService.createPersonaMessageFromProduct(requestDto.getIngredient()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
