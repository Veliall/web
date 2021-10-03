package org.example.app.domain;

public class Card {
  private long id;
  private long number;
  private long balance;

  public Card(long id, long number, long balance) {
    this.id = id;
    this.number = number;
    this.balance = balance;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getNumber() {
    return number;
  }

  public void setNumber(long number) {
    this.number = number;
  }

  public long getBalance() {
    return balance;
  }

  public void setBalance(long balance) {
    this.balance = balance;
  }
}
