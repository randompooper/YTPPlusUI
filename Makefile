.PHONY: all jar clean run

YTPPlusClass = ../YTPPlus/YTPPlus.jar
OUTPUT = YTPPlusUI.jar
PACKAGE = ytpplusui
LOC = src/$(PACKAGE)
_CLASS = MainApp FXMLController EffectsController SettingsController SaveableTextField SaveableCheckBox
CLASS = $(patsubst %,$(LOC)/%.class,$(_CLASS))
UI = FXML.fxml EffectsPane.fxml SettingsPane.fxml
SOURCES = $(patsubst %,$(LOC)/%.java,$(_CLASS)) $(patsubst %,$(LOC)/%, $(UI))
COMPAT = 8
ifeq ($(wildcard ${PATH_TO_FX}/javafx.base.jar),)
	# JavaFX 8>=
	# Might be not needed at all
	JAVAFX = ${PATH_TO_FX}/jfxswt.jar:${PATH_TO_FX}/ext/jfxrt.jar
else
	# JavaFX 8<
	JAVAFX = ${PATH_TO_FX}/javafx.base.jar:${PATH_TO_FX}/javafx.graphics.jar:${PATH_TO_FX}/javafx.controls.jar:${PATH_TO_FX}/javafx.fxml.jar
	JAVAFX_RUN = --module-path ${PATH_TO_FX} --add-modules javafx.base,javafx.controls,javafx.fxml
endif

$(OUTPUT): $(SOURCES)
	javac -source $(COMPAT) -target $(COMPAT) -classpath $(YTPPlusClass):$(JAVAFX) $(LOC)/*.java
	cd src && jar cmfv ../manifest.mf ../$(OUTPUT) $(PACKAGE)/*.class $(PACKAGE)/*.fxml

clean:
	rm $(OUTPUT) $(LOC)/*.class || true

jar: $(OUTPUT)

run-nocheck:
	exec java -classpath $(OUTPUT):$(YTPPlusClass):$(JAVAFX) $(JAVAFX_RUN) ytpplusui.MainApp

run: jar run-nocheck

all: jar
