# d &nbsp; v &nbsp;&nbsp;&nbsp; e &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; r &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; b

dattorro reverb for fx mod

while attempting to emulate jon dattorros [reverb algo](https://ccrma.stanford.edu/~dattorro/EffectDesignPart1.pdf) I stumbled upon one implemented by [@khoin](https://github.com/khoin/dx463-final/blob/b863d1982d34823f8f39df9b92e3ea948c8243c0/sdefs.sc#L150). it was similar to mine but much better. I adapted it to my needs and made it a bit more efficient to save CPU resources<sup>*</sup>. here it is packaged up as an fx mod for the [fx mod](https://github.com/sixolet/fx) framework by sixolet.

#### installation

- make sure to have fx mod installed and activated as mod. if you'd like the parameters to be formatted nicely, install [my fork](https://github.com/sonocircuit/fx/tree/param_tweeks) of the fx mod.
- install `fx_dverb` and activate via mod menu and enjoy.

#### instructions

- `level` sets the level of the reverb signal
- `pre filter` sets damping of the input signal via one-pole low-pass filter
- `pre delay` sets pre delay in ms
- `decay time` sets the decay time. at 100% the signal cycles indefinitly between the two the reverb tanks
- `damping` sets the damping of the reverb signal within the reverb tanks
- `mod rate` sets the lfo rate for modulating the delay time of the first allpass filter of both tanks
- `mod depth` sets the amount of modulation. at higher rates and mod depth a chorus-like effect is introduced
<br>
<br>
<sup>*</sup> all allpass filters and delays apart from the two modulating allpass filters were subsititued with non-interpolating ones. the trade-off is that the delay times are rounded to the next integer and the sound is slightly different (a tad more metallic?).
