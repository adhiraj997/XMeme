package com.crio.starter.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.crio.starter.App;
import com.crio.starter.data.MemeEntity;
import com.crio.starter.dto.Meme;
import com.crio.starter.exchange.ResponseDto;
import com.crio.starter.repository.MemeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
@SpringBootTest(classes = {App.class})
@DirtiesContext
class MemeServiceTest {

  @MockBean
  private MemeRepository memeRepository;

  @MockBean
  Page<MemeEntity> pagedResponse;

  @Autowired
  private MemeServiceImpl memeService;

  @Autowired
  private ModelMapper modelMapper;

  private ObjectMapper objectMapper;

  @BeforeEach
  void setup() {
    objectMapper = new ObjectMapper();
  }

  @Test
  public void contextLoads() throws Exception {
    assertThat(memeService).isNotNull();
  }

  @Test
  void testFindLatestMemes() throws JsonMappingException, JsonProcessingException {

    String sampleResponseJson = 
        "[{\"id\":\"1\",\"name\":\"MS Dhoni\",\"url\":\"www\",\"caption\":"
        + "\"Meme for my place\"},{\"id\":\"2\",\"name\":\"Viral Kohli\",\"url\":"
        + "\"com\",\"caption\":\"Another home meme\"}]";

    List<MemeEntity> memeEntityList = objectMapper.readValue(sampleResponseJson, 
            new TypeReference<List<MemeEntity>>() {});

    // modelMapper = new ModelMapper();

    List<Meme> memeList = modelMapper.map(memeEntityList, 
            new TypeToken<List<Meme>>() {}.getType());

    // List<Meme> memeList = new ArrayList<>(); 

    // for (MemeEntity memeEntity : memeEntityList) { 
    //   System.out.println(modelMapper.map(memeEntity, Meme.class));
    //   memeList.add(modelMapper.map(memeEntity, Meme.class));
    //   System.out.println(memeList);
    // }
    // memeList = modelMapper.map(memeEntityList, 
    //     new TypeToken<List<Meme>>() {}.getType());

    // Page<MemeEntity> pagedResponse = new PageImpl<>(memeEntityList);

    Mockito.when(memeRepository.findAll(any(PageRequest.class)))
        .thenReturn(pagedResponse);

    Mockito.when(pagedResponse.getContent())
        .thenReturn(memeEntityList);

    ResponseDto actualResponseDto = memeService.findLatestMemes();
    ResponseDto expectedResponseDto = new ResponseDto(memeList);

    verify(memeRepository, times(1))
        .findAll(any(PageRequest.class));

    assertEquals(expectedResponseDto.getMemes().size(), actualResponseDto.getMemes().size());
    
    
  }

  @Test
  void testFindMemeByIdIfPresent() { 

    MemeEntity sampleMemeEntity = new MemeEntity("2", "Dhoni", "Meme for my place", "www");
    Optional<MemeEntity> optionalMemeEntity = Optional.of(sampleMemeEntity);
    final Meme expectedMeme = modelMapper.map(sampleMemeEntity, Meme.class);

    Mockito.when(memeRepository.findById(any(String.class))).thenReturn(optionalMemeEntity);

    ArgumentCaptor<String> argumentCaptor = ArgumentCaptor
        .forClass(String.class);

    Meme actualMeme = memeService.findMemeById("2");

    verify(memeRepository, times(1))
        .findById(argumentCaptor.capture());

    assertEquals("2", argumentCaptor.getValue());
    assertEquals(expectedMeme.getName(), actualMeme.getName());
    assertEquals(expectedMeme.getCaption(), actualMeme.getCaption());
    assertEquals(expectedMeme.getUrl(), actualMeme.getUrl());

  }

  @Test
  void testFindMemeByIdIfNotPresent() { 

    Optional<MemeEntity> optionalMemeEntity = Optional.empty();

    Mockito.when(memeRepository.findById(any(String.class))).thenReturn(optionalMemeEntity);

    ArgumentCaptor<String> argumentCaptor = ArgumentCaptor
        .forClass(String.class);

    Meme actualMeme = memeService.findMemeById("3");

    verify(memeRepository, times(1))
        .findById(argumentCaptor.capture());

    assertEquals("3", argumentCaptor.getValue());
    assertEquals(null, actualMeme);
    
  } 

  @Test
  void testCreateMemeIfNotPresent() { 

    Meme sampleMeme = new Meme("2", "Dhoni", "Meme sample", "url");

    Mockito.when(memeRepository.findByNameAndCaptionAndUrl(any(String.class), 
        any(String.class), any(String.class))).thenReturn(new ArrayList<MemeEntity>());

    ArgumentCaptor<String> argumentCaptor = 
        ArgumentCaptor.forClass(String.class);

    String id = memeService.createMeme(sampleMeme);

    verify(memeRepository, times(1))
        .findByNameAndCaptionAndUrl(argumentCaptor.capture(), any(String.class), any(String.class));

    assertEquals("Dhoni", argumentCaptor.getValue());
    assertEquals(sampleMeme.getId(), id);
    
  }

  @Test
  void testCreateMemeIfPresent() { 

    Meme sampleMeme = new Meme("2", "Dhoni", "Meme sample", "url");
    MemeEntity memeEntity = modelMapper.map(sampleMeme, MemeEntity.class);
    List<MemeEntity> memeEntityList = new ArrayList<MemeEntity>(Arrays.asList(memeEntity));

    Mockito.when(memeRepository.findByNameAndCaptionAndUrl(any(String.class), 
        any(String.class), any(String.class))).thenReturn(memeEntityList);

    ArgumentCaptor<String> argumentCaptor = 
        ArgumentCaptor.forClass(String.class);

    String id = memeService.createMeme(sampleMeme);

    verify(memeRepository, times(1))
        .findByNameAndCaptionAndUrl(argumentCaptor.capture(), any(String.class), any(String.class));

    assertEquals("Dhoni", argumentCaptor.getValue());
    assertEquals("", id);
    
  }

}