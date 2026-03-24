local fx = require("fx/lib/fx")
local md = require 'core/mods'

local FxDverb = fx:new{
    subpath = "/fx_dverb"
}

local function round_form(param, quant, form)
  return(util.round(param, quant)..form)
end

function FxDverb:add_params()
  params:add_group("fx_dverb", "fx dverb", 9)
  FxDverb:add_slot("fx_dverb_slot", "slot")
  FxDverb:add_control("fx_dverb_level", "level", "amp", controlspec.new(0, 1, "lin", 0, 1), function(param) return round_form(param:get() * 100, 1, "%") end)
  FxDverb:add_control("fx_dverb_pre_filter", "pre filter", "preFilter", controlspec.new(0, 1, "lin", 0, 0.12), function(param) return round_form(param:get() * 100, 1, "%") end)
  FxDverb:add_control("fx_dverb_pre_delay", "pre delay", "preDelay", controlspec.new(0, 500, "lin", 0, 30, "", 1/500), function(param) return round_form(param:get(), 1, "ms") end)
  FxDverb:add_control("fx_dverb_decay", "decay time", "decayRate", controlspec.new(0, 1, "lin", 0, 0.72), function(param) return round_form(param:get() * 100, 1, "%") end)
  FxDverb:add_control("fx_dverb_damp", "damping", "damping", controlspec.new(0, 1, "lin", 0, 0.40), function(param) return round_form(param:get() * 100, 1, "%") end)
  FxDverb:add_control("fx_dverb_mod_rate", "mod rate", "modRate", controlspec.new(0.1, 3.6, "exp", 0, 1.2), function(param) return round_form(param:get(), 0.01, " hz") end)
  FxDverb:add_control("fx_dverb_mod_depth", "mod depth", "modDepth", controlspec.new(0, 1, "lin", 0, 0.32), function(param) return round_form(param:get() * 100, 1, "%") end)
end

local function add_fx()
  FxDverb:add_params()
end

md.hook.register("script_post_init", "fx dverb mod post init", add_fx)

return FxDverb