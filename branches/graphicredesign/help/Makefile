date = `date +%y%m%d%H%M%S`
t2t_files = \
	faq.t2t \
	help.t2t

html_files = $(t2t_files:.t2t=.html)

all: $(html_files)

%.html: %.t2t
	txt2tags -t html --toc --outfile $@ $<

clean:
	rm -f $(html_files)

