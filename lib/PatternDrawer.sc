PatternDrawer {
	classvar
	<>config,
	<>presets,
	<grid,
	<quant,
	<fps;

	*new {
		arg collection, cfg;
		this.presets = collection;
		this.config = cfg;
		this.init;
	}

	*init {
		var sequencer;
		sequencer = config.at("sequencer");
		quant = sequencer.at("quant").asFloat;
		grid = sequencer.at("grid").asFloat;
		fps = sequencer.at("fps").asFloat / 100;
	}

	*getStream {
		arg patterns, midiOutput;
		var rhythm = this.draw(patterns, midiOutput);
		^Ppar(rhythm);
	}

	*getChannel {
		arg touch;
		^this.config.at("channels").at(touch.asString).asInteger - 1;
	}

	*getSequenceFromPattern {
		arg pattern;
		var sequence = [];
		pattern.do({
		    arg index, i;
			sequence = sequence.addAll(presets.at(index));
		});
		^sequence;
	}

	*draw {
		arg patterns, midiOutput;
		var rhythm = ();
		patterns.pairsDo({
			arg touch, pattern;
			var sequence = this.createSequence(touch, pattern);
			var midiSequence = (sequence <> (type: \midi, midiout: midiOutput));
			rhythm.put(touch, midiSequence);
		});
		^rhythm;
	}

	*convertSequence {
		arg sequence, channel;
		^sequence.collect({
			arg item, i;
			if (item == 0) {
				0;
			} {
				channel;
			};
		});
	}

	// CALL FIRST IN DA STACK
	*createSequence {
		arg touch, pattern;
		var sequences, channel, p;
		sequences = this.getSequenceFromPattern(pattern);
		channel = this.getChannel(touch);
		^this.createPattern(channel, sequences);
	}

	*createPattern {
		arg channel, sequence;
		var converted = this.convertSequence(sequence, channel);
		^Pbind(
			\type, \midi,
			\midicmd, \noteOn,
			\chan, Pseq(converted, inf),
			\midinote, Pseq([24], inf),
			\dur, (1 / quant) * grid,
			\amp, Pseq(sequence ,inf) // ACCENT!
		);
	}
}