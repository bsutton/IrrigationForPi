package au.org.noojee.irrigation.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="tblUser")
public class User
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(unique=true)
	String name;
	String desription;
	String password;
	boolean isAdministrator;

	public long getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
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

	@Override
	public String toString()
	{
		return "User [id=" + id + ", name=" + name + ", desription=" + desription 
				+ ", isAdministrator=" + isAdministrator + "]";
	}


}
