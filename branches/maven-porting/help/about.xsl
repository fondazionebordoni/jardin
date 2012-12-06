<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml">
	<xsl:output method="html" indent="yes" encoding="utf-8"
		doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN" version="1.0"
		doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" />

	<xsl:template match="/">

		<html>
			<head>
				<title>Info</title>
			</head>

			<body style="font-size: 10pt; font-family: Arial, sans-serif;">
				<xsl:apply-templates />
			</body>
		</html>
	</xsl:template>

	<xsl:template match="info">

		<div style="float:left; width:80px;">
			<img src="../images/jardin.png"/>
		</div>

		<div style="float:left: margin-left:80px;">
			<p><b><xsl:value-of select="name" /></b><br/>
      Versione 1.6 r.<xsl:value-of select="svn/@revision" /><br/>
      Copyright (c) 2009, 2010 <xsl:value-of select="company" /></p>
		</div>

	</xsl:template>

</xsl:stylesheet>