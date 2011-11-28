package calico.plugins;

import java.util.Vector;

public class PluginCommandParameters
{
	private Vector<String> params = null;
	
	public PluginCommandParameters(Vector<String> params)
	{
		this.params = params;
	}
	
	public String getString(int index)
	{
		return this.params.get(index);
	}
	
	public Vector<String> getVector()
	{
		return this.params;
	}
	
	
	
}
