package org.example.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.app.domain.User;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransferRequestDto {
  private int addresseeCardId;
  private int sum;
}
