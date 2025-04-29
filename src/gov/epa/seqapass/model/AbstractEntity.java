package gov.epa.seqapass.model;

import java.io.Serializable;


import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;


public abstract class AbstractEntity implements Serializable {

	private static final long serialVersionUID = 5466466325016378576L;

	private Integer id;

	private Integer version;

	@Override
	public boolean equals(Object object) {
		return EqualsBuilder.reflectionEquals(this, object);
	}

	public Integer getId() {
		return id;
	}

	public Integer getVersion() {
		return version;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

}
