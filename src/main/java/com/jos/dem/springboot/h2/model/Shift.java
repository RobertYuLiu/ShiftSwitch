package com.jos.dem.springboot.h2.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@ToString
@Entity
@Table(name="shift")
@XmlRootElement
public class Shift {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	public Shift(String badgeId, LocalDate shiftDate, String shiftCode, String pit, String game, String description) {
		this.badgeId = badgeId;
		this.shiftDate = shiftDate;
		this.shiftCode = shiftCode;
		this.pit = pit;
		this.game = game;
		this.description = description;
	}

	@Column(name="badgeId")
	private String badgeId;
	private LocalDate shiftDate;
//	private Enum shiftCode;
	private String shiftCode;
	private String pit;
	private String game;
	private String description;

}
