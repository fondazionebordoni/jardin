<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.1" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:output method="xml" indent="yes"/>
	<xsl:param name="versionParam" select="2.0"/> 

	<xsl:template match="/">
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

			<fo:layout-master-set>

				<fo:simple-page-master master-name="A4"
					page-width="210mm"   page-height="297mm"
					margin="1cm 1cm">
					<fo:region-body margin-top="1cm"/>
					<fo:region-before extent="2cm"/>
				</fo:simple-page-master>

			</fo:layout-master-set>

			<fo:page-sequence master-reference="A4">

				<fo:static-content flow-name="xsl-region-before">
					<fo:block font-size="14pt">$$TITLE$$</fo:block>
				</fo:static-content>

				<fo:flow flow-name="xsl-region-body">
					<xsl:apply-templates select="//item"/>
				</fo:flow>

			</fo:page-sequence>

		</fo:root>
	</xsl:template>

	<xsl:template match="item">
		<fo:block 
			font-size="11pt"
			background-color="#eeeeee" margin-bottom="2mm"
			padding-before="2mm" padding-after="2mm" padding-start="2mm"  padding-end="2mm">
$$ROW_BEGIN$$
<fo:table table-layout="fixed" width="100%">
	<fo:table-column column-width="25%"/>
	<fo:table-column column-width="25%"/>
	<fo:table-column column-width="25%"/>
	<fo:table-column column-width="25%"/>
	<fo:table-body>
		<fo:table-row>
			<fo:table-cell padding-bottom="2mm" padding-left="4mm" padding-right="4mm">
				<fo:block font-size="9pt">$$LABEL_1$$:</fo:block>
				<fo:block background-color="#ffffff"
					font-weight="bold" padding-top="1mm" padding-right="1mm" padding-left="1mm">
					<xsl:value-of select="$$VALUE_1$$"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-bottom="2mm" padding-left="4mm" padding-right="4mm">
				<fo:block font-size="9pt">$$LABEL_2$$:</fo:block>
				<fo:block background-color="#ffffff"
					font-weight="bold" padding-top="1mm" padding-right="1mm" padding-left="1mm">
					<xsl:value-of select="$$VALUE_2$$"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-bottom="2mm" padding-left="4mm" padding-right="4mm">
				<fo:block font-size="9pt">$$LABEL_3$$:</fo:block>
				<fo:block background-color="#ffffff"
					font-weight="bold" padding-top="1mm" padding-right="1mm" padding-left="1mm">
					<xsl:value-of select="$$VALUE_3$$"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-bottom="2mm" padding-left="4mm" padding-right="4mm">
				<fo:block font-size="9pt">$$LABEL_4$$:</fo:block>
				<fo:block background-color="#ffffff"
					font-weight="bold" padding-top="1mm" padding-right="1mm" padding-left="1mm">
					<xsl:value-of select="$$VALUE_4$$"/>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</fo:table-body>
</fo:table>
$$ROW_END$$
		</fo:block>	
		<fo:block break-after="page"></fo:block>
	</xsl:template>
</xsl:stylesheet>
