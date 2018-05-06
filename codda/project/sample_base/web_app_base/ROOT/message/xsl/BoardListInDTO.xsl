<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
  <html>
  <body>
  <h2><xsl:value-of select="sinnori_message/desc"/></h2>
  <table border="1">
    <tr bgcolor="#9acd32">
      <th>메시지 식별자</th>
      <th>방향성</th>
    </tr>
	<tr>
      <td><xsl:value-of select="sinnori_message/messageID"/></td>
      <td><xsl:value-of select="sinnori_message/direction"/></td>
    </tr>
  </table><br/>

	<table border="1">
    <tr bgcolor="#9acd32">
      <th>항목 이름</th>
      <th>항목 타입</th>
		<th>항목 설명</th>
    </tr>
<xsl:for-each select="sinnori_message/singleitem">	 	
	<tr>
      <td><xsl:value-of select="@name"/></td>
      <td><xsl:value-of select="@type"/></td>
		<td><xsl:value-of select="desc"/></td>
    </tr>	
</xsl:for-each>
  </table>
  </body>
  </html>
</xsl:template>
</xsl:stylesheet> 
