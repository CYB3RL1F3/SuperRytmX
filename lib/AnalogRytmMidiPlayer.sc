AnalogRytmMidiPlayer {
	classvar
	<>config,
	<connected = false,
	<midiInput,
	<midiOutput,
	<>patternDrawer,
	// main task playing midi
	<>stream;

	*new {
		arg cfg, ptrnDrwr;
		this.init(cfg, ptrnDrwr);
	}

	*init {
		arg cfg, ptrnDrwr;
		this.config = cfg;
		this.patternDrawer = ptrnDrwr;
		this.connect;
	}

	*connect {
		MIDIClient.init(verbose: true);
		this.connectIn;
		this.connectOut;
		this.midiInput.dump;
		this.midiOutput.dump;
		MIDISynchronizedClock.init;
	}

	*connectIn {
		var port = config.at("ports").at("in");
		var source = "(" ++ port.at("name") ++ ")";
		var searchSourceBy = port.at("searchBy");
		// connect to MIDI IN
		MIDIClient.sources.do({ arg item, i;
			var src = item.name;
			if (searchSourceBy == "device") {
				src = item.device;
			};
			src.postln;
			source.postln;
			if (source.matchRegexp(src)) {
				midiInput = item;
				MIDIIn.connect(i, item);
				item.postln;
			};
		});
	}

	*connectOut {
		var port = config.at("ports").at("out");
		var destination = "(" ++ port.at("name") ++ ")";
		var searchDestinationBy = port.at("searchBy");
		destination.postln;
		searchDestinationBy.postln;
		// connect to MIDI OUT
		MIDIClient.destinations.do({ arg item, i;
			var dest = item.name, m;
			if (searchDestinationBy == "device") {
				dest = item.device;
			};
			if (destination.matchRegexp(dest)) {
				midiOutput = MIDIOut.new(i, item.uid);
				midiOutput.connect(i, item);
				// midiOutput.latency = port.at("latency").asFloat;
			};
		});
	}

	*disconnect {
		this.stop;
		MIDIIn.disconnectAll;
		MIDIClient.disposeClient;
	}

	*getQuant {
		var sequencer, fps, grid, quant;
		sequencer = this.config.at("sequencer");
		fps = sequencer.at("fps").asFloat / 100;
		grid =  sequencer.at("grid").asFloat;
		quant = sequencer.at("quant").asFloat;
		^fps * (grid / quant)
	}

	*play {
		arg patterns;
		this.stream = this.patternDrawer.getStream(patterns, this.midiOutput);
		this.stream.dump;
		if (this.stream.notNil && MIDIClient.initialized) {
			this.stream.play(MIDISynchronizedClock, quant: this.getQuant());
		}
	}

	*stop {
		if (this.stream) {
			this.stream.stop(MIDISynchronizedClock);
		};
	}

	*clearStream {
		this.stream = nil;
	}
}
