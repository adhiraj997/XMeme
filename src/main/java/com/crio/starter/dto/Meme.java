package com.crio.starter.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Meme {

  @JsonIgnore
  String id;
  
  @NotNull
  String name;

  @NotNull
  String caption;

  @NotNull
  String url;
    
}