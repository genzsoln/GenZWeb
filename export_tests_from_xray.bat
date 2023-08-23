

mvn -X clean install exec:java -Dexec.mainClass="com.bnpp.xray.ExportTests" -Dexec.classpathScope=test -e -DskipTests
