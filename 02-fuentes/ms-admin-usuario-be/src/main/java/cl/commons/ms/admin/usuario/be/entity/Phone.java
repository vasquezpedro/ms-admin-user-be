package cl.commons.ms.admin.usuario.be.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "usr_phone")
public class Phone {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "ph_number", nullable = false, length = 9)
	private Long number;
	@Column(name = "ph_city_code", nullable = false, length = 3)
	private int cityCode;
	@Column(name = "ph_contry_code", nullable = false, length = 3)
	private String contryCode;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ph_user_id")
    private User user;
	
	public Phone(){
	
	}
	public Phone(Long id,Long number,int cityCode,String contryCode,User user){
		this.id=id;
		this.number=number;
		this.cityCode=cityCode;
		this.contryCode=contryCode;
		this.user=user;
	}

	public Phone(Long number,int cityCode,String contryCode,User user){
		this.number=number;
		this.cityCode=cityCode;
		this.contryCode=contryCode;
		this.user=user;
	}

	public Phone(Long number,int cityCode,String contryCode){
		this.number=number;
		this.cityCode=cityCode;
		this.contryCode=contryCode;
	}

}

  
