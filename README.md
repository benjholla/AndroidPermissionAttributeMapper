Android Permission Attribute Mapper
====================================

Quick and dirty code to recover the mapping of Android Permissions to their corresponding Permission Group's from the Android source.  This code also recovers a mapping of Android Permission Protection Levels to Permissions.

To run the mapper invoke the JAR file from the command line.  The first argument is the input AndroidManifest.xml file to extract the mappings from.  The second two arguments are the output file paths to save the results.

    java -jar mapper.jar ./AndroidManifest.xml ./permission_group_to_permission_mapping.txt ./protection_level_to_permission_mapping.txt
