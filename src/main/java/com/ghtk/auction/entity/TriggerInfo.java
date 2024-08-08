package com.ghtk.auction.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TriggerInfo implements Serializable {
	
	private int triggerCount;  // so lan  thuc hien
	private boolean isRunForever; // chay mai khong ?
	private Long timeInterval; // khoang cach moi lan
	private int initialOffSet; // bat dau luc nao
	private String info; // info ...
}
