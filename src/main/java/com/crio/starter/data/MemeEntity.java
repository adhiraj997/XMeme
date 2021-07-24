package com.crio.starter.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@EntityScan
@Data
@Document(collection = "memes")
@NoArgsConstructor
@AllArgsConstructor
public class MemeEntity {

  @Id
  private String id;

  private String name;

  private String caption;

  private String url;

}