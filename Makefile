# Copyright 2019 randompooper
# This file is part of YTPPlusUI.
#
# YTPPlusUI is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# any later version.
#
# YTPPlusUI is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with YTPPlusUI.  If not, see <https://www.gnu.org/licenses/>.

.PHONY: all jar clean run

YTPPlusClass = ../YTPPlus/YTPPlus.jar
OUTPUT = YTPPlusUI.jar
PACKAGE = ytpplusui
LOC = src/$(PACKAGE)
_CLASS = MainApp FXMLController SettingsWindow SettingsOption PathElement
CLASS = $(patsubst %,$(LOC)/%.class,$(_CLASS))
UI = FXML.fxml
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
