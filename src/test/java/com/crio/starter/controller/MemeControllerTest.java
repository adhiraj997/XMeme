package com.crio.starter.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.crio.starter.dto.Meme;
import com.crio.starter.exchange.ResponseDto;
import com.crio.starter.service.MemeService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponentsBuilder;


@AutoConfigureMockMvc
@SpringBootTest
class MemeControllerTest {

  @Autowired
  private MemeController memeController;

  @Autowired
  private MockMvc mvc;

  @MockBean
  private MemeService memeService;

  private ObjectMapper objectMapper;

  @BeforeEach
  void setup() {
    objectMapper = new ObjectMapper();
  }

  @Test
  public void contextLoads() throws Exception {
    assertThat(memeController).isNotNull();
  }

  @Test
  public void correctQueryReturnsOkResponseAndListofMemes() throws Exception {
    String sampleResponseJson = 
            "[{\"id\":\"1\",\"name\":\"MS Dhoni\",\"url\":\"www\",\"caption\":"
            + "\"Meme for my place\"},{\"id\":\"2\",\"name\":\"Viral Kohli\",\"url\":"
            + "\"com\",\"caption\":\"Another home meme\"}]";
    
    // Meme meme = objectMapper.readValue(sampleResponseJson, Meme.class);
    // List<Meme> listOfMemes = new ArrayList<>(Arrays.asList(meme));
    List<Meme> listOfMemes = objectMapper.readValue(sampleResponseJson, 
        new TypeReference<List<Meme>>() {});
    
    Mockito.when(memeService.findLatestMemes()).thenReturn(new ResponseDto(listOfMemes));

    URI uri = UriComponentsBuilder
        .fromPath("/memes")
        .build()
        .toUri();

    MockHttpServletResponse response = mvc.perform(
        get(uri.toString()).accept(APPLICATION_JSON_VALUE)
    ).andReturn().getResponse();

    verify(memeService, times(1))
        .findLatestMemes();

    assertEquals(HttpStatus.OK.value(), response.getStatus());
    
  }

  @Test
  public void memeIdPresentReturnsOkResponse() throws Exception {

    Meme sampleMeme = new Meme("2", "Dhoni", "Meme for my place", "www");

    ArgumentCaptor<String> argumentCaptor = ArgumentCaptor
        .forClass(String.class);

    Mockito.when(memeService.findMemeById(any(String.class))).thenReturn(sampleMeme);

    URI uri = UriComponentsBuilder
        .fromPath("/memes/{id}")
        .build()
        .expand("2")
        .encode()
        .toUri(); 

    MockHttpServletResponse response = mvc.perform(
        get(uri.toString()).accept(APPLICATION_JSON_VALUE)
    ).andReturn().getResponse();

    verify(memeService, times(1))
        .findMemeById(argumentCaptor.capture());

    assertEquals(argumentCaptor.getValue(), "2");

    assertEquals(HttpStatus.OK.value(), response.getStatus());
    
  }

  @Test
  public void memeIdNotPresentReturnsNotFoundResponse() throws Exception {

    Meme sampleMeme = null;

    ArgumentCaptor<String> argumentCaptor = ArgumentCaptor
        .forClass(String.class);

    Mockito.when(memeService.findMemeById(any(String.class))).thenReturn(sampleMeme);

    URI uri = UriComponentsBuilder
        .fromPath("/memes/{id}")
        .build()
        .expand("3")
        .encode()
        .toUri(); 

    MockHttpServletResponse response = mvc.perform(
        get(uri.toString()).accept(APPLICATION_JSON_VALUE)
    ).andReturn().getResponse();


    verify(memeService, times(1))
        .findMemeById(argumentCaptor.capture());

    assertEquals(argumentCaptor.getValue(), "3"); 

    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

  }

  @Test
  public void postNewMemeReturnsOkResponseAndId() throws Exception { 

    Mockito.when(memeService.createMeme(any(Meme.class))).thenReturn("2");

    //Meme sampleMeme = new Meme("Dhoni", "Meme sample", "url");

    ArgumentCaptor<Meme> argumentCaptor = ArgumentCaptor
        .forClass(Meme.class);

    URI uri = UriComponentsBuilder
        .fromPath("/memes")
        .build()
        .toUri();

    // MockHttpServletResponse response = mvc.perform(
    //     post(uri.toString()).content("{ \"name\": \"Dhoni\", 
    //     \"caption\": \"Meme sample\", \"url\": \"www\"}")
    //     .accept(APPLICATION_JSON_VALUE))
    //     .andReturn().getResponse();

    // Meme meme = new Meme("2", "Dhoni", "Sample meme", "www");
    // String memeAsJsonString = objectMapper.writeValueAsString(meme);

    final MockHttpServletResponse response = mvc.perform(
        post(uri.toString())
        .content("{ \"name\": \"Dhoni\", \"caption\": \"Sample meme\", \"url\": \"www\"}")
        .contentType(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();

    verify(memeService, times(1))
        .createMeme(argumentCaptor.capture());

    assertEquals("Dhoni", argumentCaptor.getValue().getName());
    assertEquals("Sample meme", argumentCaptor.getValue().getCaption());
    assertEquals("www", argumentCaptor.getValue().getUrl());

    assertEquals(HttpStatus.CREATED.value(), response.getStatus());

  }

  @Test
  public void postExistingMemeReturnsConflictResponse() throws Exception {

    Mockito.when(memeService.createMeme(any(Meme.class))).thenReturn("");

    ArgumentCaptor<Meme> argumentCaptor = ArgumentCaptor
        .forClass(Meme.class);

    URI uri = UriComponentsBuilder
        .fromPath("/memes")
        .build()
        .toUri();

    final MockHttpServletResponse response = mvc.perform(
        post(uri.toString())
        .content("{ \"name\": \"Dhoni\", \"caption\": \"Sample meme\", \"url\": \"www\"}")
        .contentType(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();

    verify(memeService, times(1))
        .createMeme(argumentCaptor.capture());

    assertEquals("Dhoni", argumentCaptor.getValue().getName());
    assertEquals("Sample meme", argumentCaptor.getValue().getCaption());
    assertEquals("www", argumentCaptor.getValue().getUrl());

    assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());

  }

  // @Test
  // void sayHello() throws Exception {
  //   //given
  //   Mockito.doReturn(new ResponseDto("Hello Java"))
  //       .when(greetingsService).getMessage("001");

  //   // when
  //   URI uri = UriComponentsBuilder
  //       .fromPath("/say-hello")
  //       .queryParam("messageId", "001")
  //       .build().toUri();

  //   MockHttpServletResponse response = mvc.perform(
  //       get(uri.toString()).accept(APPLICATION_JSON_VALUE)
  //   ).andReturn().getResponse();

  //   //then
  //   String responseStr = response.getContentAsString();
  //   ObjectMapper mapper = new ObjectMapper();
  //   ResponseDto responseDto = mapper.readValue(responseStr, ResponseDto.class);
  //   ResponseDto ref = new ResponseDto("Hello Java");

  //   assertEquals(responseDto, ref);
  //   Mockito.verify(greetingsService, Mockito.times(1)).getMessage("001");
  // }
}