package nl.tucuxi.faceboard.shared;

import com.vercer.engine.persist.annotation.Key;

public class RemovalKey
{
	
	public String getDskey() {
		return dskey;
	}
	public void setDskey(String dskey) {
		this.dskey = dskey;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	@Key String dskey;
	String value;
}
