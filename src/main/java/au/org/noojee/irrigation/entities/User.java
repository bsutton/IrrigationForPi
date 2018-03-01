package au.org.noojee.irrigation.entities;

import java.time.Duration;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "tblUser")
public class User
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false, nullable = false)
	private long id;

	@Version
	private int version;

	@Column(unique = true)
	private String username;
	private String desription;
	private String password;
	private boolean isAdministrator;

	private String securityToken;
	private LocalDateTime tokenExpiryDate;
	private Duration tokenLivesFor;

	private String emailAddress;

	public long getId()
	{
		return id;
	}

	public String getName()
	{
		return username;
	}

	public void setName(String name)
	{
		this.username = name;
	}

	
	public String getEmailAddress()
	{
		return emailAddress;
	}

	public String getDesription()
	{
		return desription;
	}

	public void setDesription(String desription)
	{
		this.desription = desription;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public boolean isAdministrator()
	{
		return isAdministrator;
	}

	public void setAdministrator(boolean isAdministrator)
	{
		this.isAdministrator = isAdministrator;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getSecurityToken()
	{
		return securityToken;
	}

	public void setSecurityToken(String securityToken)
	{
		this.securityToken = securityToken;
	}

	public LocalDateTime getTokenExpiryDate()
	{
		return tokenExpiryDate;
	}

	public void setTokenExpiryDate(LocalDateTime tokenExpiryDate)
	{
		this.tokenExpiryDate = tokenExpiryDate;
	}

	public Duration getTokenLivesFor()
	{
		return tokenLivesFor;
	}

	public void setTokenLivesFor(Duration tokenLivesFor)
	{
		this.tokenLivesFor = tokenLivesFor;
	}

	public boolean isSecurityTokenLive()
	{
		return this.tokenExpiryDate.isAfter(LocalDateTime.now());
	}

	@Override
	public String toString()
	{
		return "User [id=" + id + ", name=" + username + ", desription=" + desription
				+ ", isAdministrator=" + isAdministrator + "]";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id != other.id)
			return false;
		return true;
	}


}
