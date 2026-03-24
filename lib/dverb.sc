FxDverb : FxBase {
	
	*new { 
		var ret = super.newCopyArgs(nil, \none, (
			amp: 1,
			preDelay: 0.001,
			preFilter: 0.1,
			decayRate: 0.8,
			damping: 0.22,
			modDepth: 0.2,
			modRate: 1
		), nil, 0.5);
		^ret;
	}
	
	*initClass {
		FxSetup.register(this.new);
	}
	
	subPath {
		^"/fx_dverb";
	}  
	
	symbol {
		^\fxDverb;
	}
	
	addSynthdefs {
		// modified version of @khoin's implementation of dattorros reverb (thank you!)
		// https://github.com/khoin/dx463-final/blob/b863d1982d34823f8f39df9b92e3ea948c8243c0/sdefs.sc#L150
		SynthDef(\fxDverb, {
			arg inBus, outBus,
			amp = 1, preDelay = 0.1, preFilter = 0.1,
			decayRate = 0.8, damping = 0.22,
			modDepth = 0.2, modRate = 1;
			
			// signals
			var dry = In.ar(inBus, 2);
			var wetL = Silent.ar;
			var wetR = Silent.ar;
			var preTank, tank, wet;
			
			// sample rate used by dattorro
			var dSR = 29761;
			
			// max excursion (samples) dattoro used 16@29khz
			var exMax = 24;
			
			// exp decay rate
			var gFacT60 = { |delay, gFac|
				gFac.sign * (-3 * delay / log10(gFac.abs));
			};
			
			// values for pre tank
			var preTankVals = [
				[0.75, 0.75, 0.625, 0.625], // gFacs
				[142, 107, 379, 277] / dSR  // times
			].flop;
			
			// values for tank part
			var tankAP1GFac = -0.64; // tail density > seems a nice spot
			var tankAP1Time = 672;
			var tankDel1    = 4453/dSR;
			var tankAP2GFac = (decayRate + 0.15).clip(0.25, 0.5); // decay2 as from paper
			var tankAP2Time = 1800/dSR;
			var tankDel2    = 3720/dSR;
			
			var tankAP3GFac = tankAP1GFac;
			var tankAP3Time = 908;
			var tankDel3    = 4217/dSR;
			var tankAP4GFac = tankAP2GFac;
			var tankAP4Time = 2656/dSR;
			var tankDel4    = 3163/dSR;
			
			// map and clamp
			damping = damping.lincurve(0, 1, 0.002, 0.998, -2.2).lag3; // remap damp to log curve
			preFilter = preFilter.linlin(0, 1, 0.002, 0.78);
			decayRate = decayRate.linlin(0, 1, 0.01, 0.99);
			
			// PreTank
			preTank = (dry[0] + dry[1]) * -6.dbamp;
			preTank = DelayN.ar(preTank, 0.5, preDelay * 0.001);
			preTank = OnePole.ar(preTank, preFilter);
			preTankVals.do({ arg pair; // 0: gFac, 1: time
				preTank = AllpassN.ar(preTank, pair[1], pair[1], gFacT60.value(pair[1], pair[0]));
			});
			
			//// reverb tank
			// first branch
			tank = AllpassC.ar(preTank + (decayRate * LocalIn.ar(1)),
				maxdelaytime: (tankAP1Time + exMax) / dSR,
				delaytime: (tankAP1Time/dSR) + ((exMax/dSR) * SinOsc.ar(modRate, 0, modDepth)),
				decaytime: gFacT60.value(tankAP1Time/dSR, tankAP1GFac)
			);
			
			wetL = -0.6 * DelayN.ar(tank, 1990/dSR, 1990/dSR) + wetL;
			wetR = 0.6 * tank + wetR;
			wetR = 0.6 * DelayN.ar(tank, 3300/dSR, 3300/dSR) + wetR;
			tank = DelayN.ar(tank, tankDel1, tankDel1);
			tank = OnePole.ar(tank, damping) * decayRate;
			wetL = -0.6 * tank + wetL;
			tank = AllpassN.ar(tank, tankAP2Time, tankAP2Time, gFacT60.value(tankAP2Time, tankAP2GFac));
			wetR = -0.6 * tank + wetR;
			tank = DelayN.ar(tank, tankDel2, tankDel2);
			wetR = 0.6 * tank + wetR;
			
			// second branch
			tank = AllpassC.ar((tank * decayRate) + preTank,
				maxdelaytime: (tankAP3Time + exMax)/dSR,
				delaytime: (tankAP3Time/dSR) + ((exMax/dSR) * SinOsc.ar(modRate * 0.8, pi, modDepth)),
				decaytime: gFacT60.value(tankAP3Time/dSR, tankAP3GFac)
			);
			
			wetL = 0.6 * tank + wetL;
			wetL = 0.6 * DelayN.ar(tank, 2700/dSR, 2700/dSR) + wetL;
			wetR = -0.6 * DelayN.ar(tank, 2100/dSR, 2100/dSR) + wetR;
			tank = DelayC.ar(tank, tankDel3, tankDel3);
			tank = OnePole.ar(tank, damping) * decayRate;
			tank = AllpassN.ar(tank, tankAP4Time, tankAP4Time, gFacT60.value(tankAP4Time, tankAP4GFac));
			wetL = -0.6 * tank + wetL;
			wetR = -0.6 * DelayN.ar(tank, 200/dSR, 200/dSR) + wetR;
			
			tank = DelayN.ar(tank, tankDel4, tankDel4);
			wetL = 0.6 * tank + wetL;
			tank = tank * decayRate;
			LocalOut.ar(tank);
			//// end of tank
			
			wet = [wetL, wetR];
			wet = HPF.ar(wet, 60);
			Out.ar(outBus, wet * amp);
		}).add;
		
	}
	
}