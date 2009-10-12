date = `date +%y%m%d%H%M%S`
pic_diagrams = \
	init_sd.pic \
	login_sd.pic \
	sd1.pic \
	sd2.pic \
	sd3.pic \
	sd4.pic \
	
pdf_diagrams = $(pic_diagrams:.pic=.pdf)

all: sequence.pic $(pdf_diagrams)

%.fig: %.pic
	pic2plot -Tfig -F "Helvetica-Narrow" -f 0.01 $< > $@

%.ps: %.pic
	pic2plot -Tps -F "Helvetica-Narrow" -f 0.01 $< > $@

%.eps: %.ps
	ps2eps -r $(resolution) $<

%.eps: %.dia
	dia -t eps-pango -e $@ $<

%.ps: %.fig
	fig2dev -Leps $< $@

%.eps: %.fig
	fig2dev -Leps $< $@

%.pdf: %.fig
	fig2dev -Lpdf $< $@

%.eps: %.gif
	convert $< $(convertoptions) $@

%.eps: %.jpg
	convert $< $(convertoptions) $@

%.pdf: %.png
	convert $< $(convertoptions) $@

%.eps: %.png
	convert $< $(convertoptions) $@

%.eps: %.svg
	inkscape -z --file=$< $(svgoptions) -D --export-eps=$@ -T -F

%.eps: %.pdf
	pdftops -eps $< $@

%.pdf: %.svg
	inkscape -z --file=$< $(svgoptions) -D --export-pdf=$@ -T -F

clean:
	rm -f *~

