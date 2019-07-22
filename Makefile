.PHONY: all jar clean run

YTPPlusClass = ../YTPPlus/YTPPlus.jar
OUTPUT = YTPPlusUI.jar
PACKAGE = ytpplusui
LOC = src/$(PACKAGE)
_CLASS = MainApp FXMLController EffectsController SettingsController
CLASS = $(patsubst %,$(LOC)/%.class,$(_CLASS))
UI = FXML.fxml EffectsPane.fxml SettingsPane.fxml
SOURCES = $(patsubst %,$(LOC)/%.java,$(_CLASS)) $(patsubst %,$(LOC)/%, $(UI))
COMPAT = -source 8 -target 8

$(OUTPUT): $(SOURCES)
	javac $(COMPAT) -classpath $(YTPPlusClass):${PATH_TO_FX}/javafx.base.jar:${PATH_TO_FX}/javafx.graphics.jar:${PATH_TO_FX}/javafx.controls.jar:/${PATH_TO_FX}/javafx.fxml.jar:${PATH_TO_FX}/javafx.media.jar $(LOC)/*.java
	cd src && jar cmfv ../manifest.mf ../$(OUTPUT) $(PACKAGE)/*.class $(PACKAGE)/*.fxml

clean:
	rm $(OUTPUT) $(LOC)/*.class || true

jar: $(OUTPUT)

run: jar
	exec java -classpath $(OUTPUT):$(YTPPlusClass) --module-path ${PATH_TO_FX} --add-modules javafx.controls,javafx.fxml,javafx.media ytpplusui.MainApp

all: jar
