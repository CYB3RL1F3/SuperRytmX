
// a "clock" that slaves itself to midi clock messages
// only one port can be the midi source; hence this is a singleton
// H. James Harkins -- jamshark70@dewdrop-world.net

MIDISyncClock {
	classvar	<>ticksPerBeat = 24;	// MIDI clock standard
	classvar	responseFuncs;

	classvar	<ticks = 0, <beats = 0, <startTime = 0,
			<tempo = 0, <beatDur = 0,
			<beatsPerBar = 4, <barsPerBeat = 0.25, <baseBar, <baseBarBeat, <>playing = false;

		// private vars
	classvar	lastTickTime, <queue;

	*initClass {

	}

	*init {
			// retrieve MIDI sources first
			// assumes sources[0] is the MIDI clock source
			// if not, you should init midiclient yourself and manually
			// assign the right port to inport == 0
			// using MIDIIn.connect(0, MIDIClient.sources[x])

		MIDIClient.initialized.not.if({
			MIDIClient.init;
			MIDIClient.sources.do({ arg src, i;
				MIDIIn.connect(i, src);		// connect it
			});
		});

		queue = PriorityQueue.new;
		beats = ticks = baseBar = baseBarBeat = 0;

		MIDIdef.start( \start, {
			startTime = lastTickTime = Main.elapsedTime;
			// ticks = beats = baseBar = baseBarBeat = 0;
			playing = true;
		});

		MIDIdef.stop( \stop, {
			playing = false;
		});

		MIDIdef.midiClock(\midiclock, {
			this.tick;
		});

		MIDIdef.continue(\continue, {
			this.playing = true;
			// this.exec;
		});
	}

	*tick {
		var	lastTickDelta, nextTime, task, tickIndex;
		nextTime = Main.elapsedTime;
		lastTickDelta = nextTime - (lastTickTime ? 0);
		lastTickTime = nextTime;
		tempo = (beatDur = lastTickDelta * ticksPerBeat).reciprocal;
		if (playing) {
			// use nextTime as temp var to calculate tempo
			// this is inherently inaccurate; tempo will fluctuate slightly around base
			ticks = ticks + 1;
			beats = ticks / ticksPerBeat;

			// while loop needed because more than one thing may be scheduled for this tick
			{ (queue.topPriority ?? { inf }) < ticks }.while({
				// perform the action, and check if it should be rescheduled
				(nextTime = (task = queue.pop).value(beats)).isNumber.if({
					this.sched(nextTime, task, -1)
				});
			});
		}
	}

	*schedAbs { arg when, task;
		queue.put(when * ticksPerBeat, task);
	}

	*sched { arg when, task, adjustment = 0;
		queue.put((when * ticksPerBeat) + ticks + adjustment, task);
	}

	*play { arg task, when;
		this.schedAbs(when.nextTimeOnGrid(this), task);
	}

	*nextTimeOnGrid { arg quant = 1, phase = 0;
		var offset;
		if (quant < 0) { quant = beatsPerBar * quant.neg };
		offset = baseBarBeat + phase;
		^roundUp(this.beats - offset, quant) + offset;
	}

	*beatsPerBar_ { |newBeatsPerBar = 4|
		this.setMeterAtBeat(newBeatsPerBar, beats)
	}

	*setMeterAtBeat { arg newBeatsPerBar, beats;
		// bar must be integer valued when meter changes or confusion results later.
		baseBar = round((beats - baseBarBeat) * barsPerBeat + baseBar, 1);
		baseBarBeat = beats;
		beatsPerBar = newBeatsPerBar;
		barsPerBeat = beatsPerBar.reciprocal;
		this.changed;
	}

	*beats2secs { |beats|
		^beats * beatDur;
	}

	*secs2beats { |seconds|
		^seconds * tempo;
	}

		// elapsed time doesn't make sense because this clock only advances when told
		// from outside - but, -play methods need elapsedBeats to calculate quant
	*elapsedBeats { ^beats }
	*seconds { ^startTime.notNil.if(Main.elapsedTime - startTime, nil) }

	*clear { queue.clear }

	*interrupt {
		this.clear;
		MIDIIn.sysrt = nil;
		queue = nil;
		beats = ticks = baseBar = baseBarBeat = 0;
	}

		// for debugging
	*dumpQueue {
		{ queue.topPriority.notNil }.while({
			Post << "\n" << queue.topPriority << "\n";
			queue.pop.dumpFromQueue;
		});
	}
}
