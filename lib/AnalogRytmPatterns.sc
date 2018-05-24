AnalogRytmPatterns {
	classvar <>presets,
	<>patterns;

	*new {
		arg fill = false;
		presets = [];
		patterns = ();
		this.init(fill);
	}

	*init {
		arg fill = false;
		if (fill) {
			this.autoFeedPresets;
			this.createDefaultPatterns;
		};
	}

	*createDefaultPatterns {
		this.setPatternToTouch(AnalogRytmTouchs.bd, [0, 0, 3, 4, 6, 0, 2]);
		this.setPatternToTouch(AnalogRytmTouchs.ch, [1, 1, 2, 1, 5, 1, 1]);
		this.setPatternToTouch(AnalogRytmTouchs.cy, [4, 6, 5, 8, 9, 5, 6]);
		this.setPatternToTouch(AnalogRytmTouchs.bt, [6, 7, 0, 4, 3, 9, 7]);
	}

	*feedPresetsWith {
		arg row;
		this.presets = presets.add(row);
	}

	// feed with "handmade" patterns
	*autoFeedPresets {
		this.feedPresetsWith([1, 0, 1, 0, 1, 0, 1, 0]);
		this.feedPresetsWith([0, 1, 0, 1, 0, 1, 0, 1]);
		this.feedPresetsWith([1, 1, 1, 1, 1, 1, 1, 1]);
		this.feedPresetsWith([1, 0, 0, 1, 0, 0, 1, 0]);
		this.feedPresetsWith([1, 0, 0, 0, 1, 0, 0, 0]);
		this.feedPresetsWith([1, 1, 0, 0, 1, 1, 0, 0]);
		this.feedPresetsWith([1, 1, 1, 0, 1, 0, 0, 0]);
		this.feedPresetsWith([1, 0, 1, 1, 1, 0, 1, 1]);
		this.feedPresetsWith([0, 1, 1, 0, 0, 1, 1, 0]);
		this.feedPresetsWith([1, 1, 0, 0, 1, 1, 0, 0]);
		this.feedPresetsWith([1, 1, 1, 0, 0, 1, 0, 0]);
		this.feedPresetsWith([1, 0, 1, 0, 1, 1, 0, 0]);
		this.feedPresetsWith([1, 1, 1, 0, 0, 1, 0, 1]);
		this.feedPresetsWith([1, 0, 1, 1, 1, 0, 1, 1]);
		this.feedPresetsWith([1, 0, 0, 1, 0, 1, 0, 0]);
		this.feedPresetsWith([1, 1, 1, 0, 0, 1, 1, 0]);
		this.feedPresetsWith([1, 1, 0, 0, 1, 0, 0, 0]);
		this.feedPresetsWith([1, 1, 0, 1, 0, 0, 0, 0]);
		this.feedPresetsWith([0, 0, 0, 0, 0, 0, 0, 0]);
		this.feedPresetsWith([0, 0, 1, 0, 1, 0, 0, 1]);
		this.feedPresetsWith([1, 1, 1, 0, 0, 0, 1, 1]);
	}

	*setPatternToTouch {
		arg touch, rows;
		this.patterns = patterns.put(touch, rows);
	}

	*clearPatterns {
		this.patterns = ();
	}

	*clearPresets {
		this.presets = [];
	}
}