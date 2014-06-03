import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class Main {

	/**
	 * Expects 3 command line parameters
	 * Argument 1 - Input file path the AndroidManifest.xml to parse
	 * Note: To recover permissions in the Android OS use the AndroidManifest.xml file from
	 *       the source code of the version of you want.
	 *       Example: https://github.com/android/platform_frameworks_base/blob/master/core/res/AndroidManifest.xml
	 * Argument 2 - Output file path to save the permission group to permissions mapping
	 * Argument 3 - Output file path to save the protection level to permissions mapping
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// read the file path from command line args
			File androidManifestFile = new File(args[0]);
			
			// parse manifest xml file
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document androidManifest = dBuilder.parse(androidManifestFile);
			androidManifest.getDocumentElement().normalize();
			
			// for each permission entry record the permission group 
			// as a key and the permission as a value
			HashMap<String,HashSet<String>> permissionGroupToPermissionsMapping = new HashMap<String,HashSet<String>>();
			
			// for each permission entry record the permission 
			// as a key and the protection level as a value
			HashMap<String,HashSet<String>> protectionLevelToPermissionMapping = new HashMap<String,HashSet<String>>();
			
			// iterate over permissions and collect attributes
			NodeList permissions = androidManifest.getElementsByTagName("permission");
			for (int i = 0; i < permissions.getLength(); i++) {
				Element permission = (Element) permissions.item(i);
				String permissionName = permission.getAttribute("android:name");
				String permissionGroup = permission.getAttribute("android:permissionGroup");
				String protectionLevelString = permission.getAttribute("android:protectionLevel");
				
				// add the permission group mapping for permission
				if(permissionGroupToPermissionsMapping.containsKey(permissionGroup)){
					permissionGroupToPermissionsMapping.get(permissionGroup).add(permissionName);
				} else {
					HashSet<String> permissionNames = new HashSet<String>();
					permissionNames.add(permissionName);
					permissionGroupToPermissionsMapping.put(permissionGroup, permissionNames);
				}
				
				// add the protection level mapping for permission
				for(String protectionLevel : protectionLevelString.split("\\|")){
					if(protectionLevelToPermissionMapping.containsKey(protectionLevel)){
						protectionLevelToPermissionMapping.get(protectionLevel).add(permissionName);
					} else {
						HashSet<String> permissionNames = new HashSet<String>();
						permissionNames.add(permissionName);
						protectionLevelToPermissionMapping.put(protectionLevel, permissionNames);
					}
				}
			}
			
			File permissionGroupMappingOutputFile = new File(args[1]);
			FileWriter fw = new FileWriter(permissionGroupMappingOutputFile);
			for(String key : permissionGroupToPermissionsMapping.keySet()){
				if(key.equals("")){
					fw.write("NO PERMISSION GROUP FOUND:\n");
				} else {
					fw.write(key + ":\n");
				}
				fw.write(permissionGroupToPermissionsMapping.get(key).size() + " Permissions\n");
				for(String value : permissionGroupToPermissionsMapping.get(key)){
					fw.write(value + "\n");
				}
			}
			fw.close();
			
			File protectionLevelMappingOutputFile = new File(args[2]);
			fw = new FileWriter(protectionLevelMappingOutputFile);
			for(String key : protectionLevelToPermissionMapping.keySet()){
				if(key.equals("")){
					fw.write("NO PROTECTION LEVEL FOUND:\n");
				} else {
					fw.write(key + ":\n");
				}
				fw.write(protectionLevelToPermissionMapping.get(key).size() + " Permissions\n");
				for(String value : protectionLevelToPermissionMapping.get(key)){
					fw.write(value + "\n");
				}
			}
			fw.close();
		} catch (Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}

}
