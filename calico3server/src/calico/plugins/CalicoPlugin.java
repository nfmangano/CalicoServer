package calico.plugins;
import calico.plugins.events.*;

public interface CalicoPlugin
{

	public void onPluginEnd();
	public void onPluginStart();
	public void onException(Exception e);
}
