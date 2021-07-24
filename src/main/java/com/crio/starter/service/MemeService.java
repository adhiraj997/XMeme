package com.crio.starter.service;

import com.crio.starter.dto.Meme;
import com.crio.starter.exchange.ResponseDto;

import org.springframework.stereotype.Service;

@Service
public interface MemeService {

  ResponseDto findLatestMemes();

  Meme findMemeById(String id);

  String createMeme(Meme meme);
    
}