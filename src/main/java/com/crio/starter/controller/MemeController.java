package com.crio.starter.controller;

import com.crio.starter.dto.Meme;
import com.crio.starter.exchange.ResponseDto;
import com.crio.starter.service.MemeService;

import java.util.List;

import javax.validation.Valid;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class MemeController {

  @Autowired MemeService memeService; 

  @GetMapping("/memes") 
  public ResponseEntity<List<Meme>> getListOfMemes() {

    ResponseDto responseDto = memeService.findLatestMemes();
    System.out.println(responseDto.getMemes().size());
    return ResponseEntity.ok().body(responseDto.getMemes());
  }

  @GetMapping("/memes/{id}") 
  public ResponseEntity<Meme> getMemeById(@PathVariable String id) {

    Meme meme = memeService.findMemeById(id);
    if (meme == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    return ResponseEntity.ok().body(meme);
  }

  @PostMapping("/memes")
  public ResponseEntity<Object> postMeme(@Valid @RequestBody Meme meme) { 

    System.out.println(meme);
    String posted = memeService.createMeme(meme);
    if (posted.equals("")) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
    }

    JSONObject jsonObject = new JSONObject();
    jsonObject.put("id", posted);
    return ResponseEntity.status(HttpStatus.CREATED).body(jsonObject.toMap());

  }
    
}