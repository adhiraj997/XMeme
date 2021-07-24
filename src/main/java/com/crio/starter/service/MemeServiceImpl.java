package com.crio.starter.service;

import com.crio.starter.data.MemeEntity;
import com.crio.starter.dto.Meme;
import com.crio.starter.exchange.ResponseDto;
import com.crio.starter.repository.MemeRepository;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class MemeServiceImpl implements MemeService { 

  @Autowired
  MemeRepository memeRepository;

  @Autowired
  ModelMapper modelMapper;

  @Override
  public ResponseDto findLatestMemes() { 

    PageRequest request = PageRequest.of(0, 100, Sort.Direction.DESC, "_id");

    // calling memeRepository.findAll(request) returns a Page<MemeEntity> object
    // to convert it to a list, we call .getContent() on it 
    List<MemeEntity> memeEntityList = memeRepository.findAll(request).getContent();

    // System.out.println(memeEntityList);
    // System.out.println(modelMapper);
        
    // ModelMapper modelMapper = new ModelMapper();
    List<Meme> memeList = modelMapper.map(memeEntityList, 
        new TypeToken<List<Meme>>() {}.getType());

    System.out.println(memeList);

    ResponseDto responseDto = new ResponseDto(memeList);

    return responseDto;
  }

  @Override
  public Meme findMemeById(String id) { 

    Optional<MemeEntity> memeEntityOptional = memeRepository.findById(id);
    // System.out.println(memeEntityOptional);
    // System.out.println(memeEntityOptional.isPresent());

    if (memeEntityOptional.isPresent()) {
      MemeEntity memeEntity = memeEntityOptional.get(); 
      Meme meme = modelMapper.map(memeEntity, Meme.class); 
      return meme;
    }

    return null;
  }

  @Override
  public String createMeme(Meme meme) { 

    System.out.println(meme);

    List<MemeEntity> memeEntityList = memeRepository.findByNameAndCaptionAndUrl(
        meme.getName(), meme.getCaption(), meme.getUrl());

    // System.out.println(memeEntityList);

    if (!memeEntityList.isEmpty()) {
      return "";
    }

    MemeEntity memeEntity = modelMapper.map(meme, MemeEntity.class);

    // MemeEntity memeEntity = new MemeEntity();
    // System.out.println(memeEntity);
    // memeEntity.setName(meme.getName());
    // memeEntity.setCaption(meme.getCaption());
    // memeEntity.setUrl(meme.getUrl());

    memeRepository.save(memeEntity);

    // System.out.println(memeEntity);
    // System.out.println(memeEntity.getId());

    // memeRepository.save(meme);
    
    return memeEntity.getId();

  }
    
}