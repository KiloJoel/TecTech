package com.github.technus.tectech.mechanics.elementalMatter.definitions.primitive;

import com.github.technus.tectech.mechanics.elementalMatter.core.cElementalDecay;
import com.github.technus.tectech.mechanics.elementalMatter.core.templates.cElementalPrimitive;

import static com.github.technus.tectech.mechanics.elementalMatter.definitions.primitive.eLeptonDefinition.*;
import static com.github.technus.tectech.mechanics.elementalMatter.definitions.primitive.eNeutrinoDefinition.*;

/**
 * Created by danie_000 on 22.10.2016.
 */
public final class eQuarkDefinition extends cElementalPrimitive {
    public static final eQuarkDefinition
            quark_u = new eQuarkDefinition("Up", "u", 1, 2.3e6D, 2, 3),
            quark_c = new eQuarkDefinition("Charm", "c", 2, 1.29e9D, 2, 9),
            quark_t = new eQuarkDefinition("Top", "t", 3, 172.44e9D, 2, 13),
            quark_d = new eQuarkDefinition("Down", "d", 1, 4.8e6D, -1, 5),
            quark_s = new eQuarkDefinition("Strange", "s", 2, 95e6D, -1, 7),
            quark_b = new eQuarkDefinition("Bottom", "b", 3, 4.65e9D, -1, 11),
            quark_u_ = new eQuarkDefinition("AntiUp", "~u", -1, 2.3e6D, -2, 4),
            quark_c_ = new eQuarkDefinition("AntiCharm", "~c", -2, 1.29e9D, -2, 10),
            quark_t_ = new eQuarkDefinition("AntiTop", "~t", -3, 172.44e9D, -2, 14),
            quark_d_ = new eQuarkDefinition("AntiDown", "~d", -1, 4.8e6D, 1, 6),
            quark_s_ = new eQuarkDefinition("AntiStrange", "~s", -2, 95e6D, 1, 8),
            quark_b_ = new eQuarkDefinition("AntiBottom", "~b", -3, 4.65e9D, 1, 12);

    private eQuarkDefinition(String name, String symbol, int type, double mass, int charge, int ID) {
        super(name, symbol, type, mass, charge, 0, ID);
    }

    public static void run() {
        quark_u.init(quark_u_, STABLE_RAW_LIFE_TIME, 3, -1,
                new cElementalDecay(1.23201e-5D, quark_b/*,lepton_t_,lepton_Vt*/),
                new cElementalDecay(0.050778116D, quark_s/*,lepton_m_,lepton_Vm*/),
                new cElementalDecay(0.9D, quark_d, lepton_e_, lepton_Ve),
                eBosonDefinition.deadEnd);//makes photons and don't care
        quark_c.init(quark_c_, 0.5e-13D, 1, -1,
                new cElementalDecay(0.00169744D, quark_b/*,lepton_t_,lepton_Vt*/),
                new cElementalDecay(0.05071504D, quark_d, lepton_m_, lepton_Vm),
                new cElementalDecay(0.9D, quark_s, lepton_e_, lepton_Ve),
                eBosonDefinition.deadEnd);//makes photons and don't care
        quark_t.init(quark_t_, 2.5e-26D, 0, -1,
                new cElementalDecay(7.51689e-5D, quark_d, lepton_t_, lepton_Vt),
                new cElementalDecay(0.00163216D, quark_s, lepton_m_, lepton_Vm),
                new cElementalDecay(0.9D, quark_b, lepton_e_, lepton_Ve),
                eBosonDefinition.deadEnd);//makes photons and don't care

        quark_d.init(quark_d_, STABLE_RAW_LIFE_TIME, 3, -1,
                new cElementalDecay(7.51689e-5D, quark_t/*,lepton_t,lepton_Vt_*/),
                new cElementalDecay(0.05071504D, quark_c/*,lepton_m,lepton_Vm_*/),
                new cElementalDecay(0.9D, quark_u, lepton_e, lepton_Ve_),
                eBosonDefinition.deadEnd);//makes photons and don't care
        quark_s.init(quark_s_, 0.6e-9D, 1, -1,
                new cElementalDecay(0.00163216D, quark_t/*,lepton_t,lepton_Vt_*/),
                new cElementalDecay(0.050778116D, quark_u, lepton_m, lepton_Vm_),
                new cElementalDecay(0.9D, quark_c, lepton_e, lepton_Ve_),
                eBosonDefinition.deadEnd);//makes photons and don't care
        quark_b.init(quark_b_, 0.7e-13D, 0, -1,
                new cElementalDecay(1.23201e-5D, quark_u, lepton_t, lepton_Vt_),
                new cElementalDecay(0.00169744D, quark_c, lepton_m, lepton_Vm_),
                new cElementalDecay(0.9D, quark_t, lepton_e, lepton_Ve_),
                eBosonDefinition.deadEnd);//makes photons and don't care

        quark_u_.init(quark_u, STABLE_RAW_LIFE_TIME, 3, -1,
                new cElementalDecay(1.23201e-5D, quark_b_/*,lepton_t,lepton_Vt_*/),
                new cElementalDecay(0.050778116D, quark_s_/*,lepton_m,lepton_Vm_*/),
                new cElementalDecay(0.9D, quark_d_, lepton_e, lepton_Ve_),
                eBosonDefinition.deadEnd);//makes photons and don't care
        quark_c_.init(quark_c, 0.5e-13D, 1, -1,
                new cElementalDecay(0.00169744F, quark_b_/*,lepton_t,lepton_Vt_*/),
                new cElementalDecay(0.05071504F, quark_d_, lepton_m, lepton_Vm_),
                new cElementalDecay(0.9F, quark_s_, lepton_e, lepton_Ve_),
                eBosonDefinition.deadEnd);//makes photons and don't care
        quark_t_.init(quark_t, 2.5e-26F, 0, -1,
                new cElementalDecay(7.51689e-5F, quark_d_, lepton_t, lepton_Vt_),
                new cElementalDecay(0.00163216F, quark_s_, lepton_m, lepton_Vm_),
                new cElementalDecay(0.9F, quark_b_, lepton_e, lepton_Ve_),
                eBosonDefinition.deadEnd);//makes photons and don't care

        quark_d_.init(quark_d, STABLE_RAW_LIFE_TIME, 3, -1,
                new cElementalDecay(7.51689e-5F, quark_t_/*,lepton_t_,lepton_Vt*/),
                new cElementalDecay(0.05071504F, quark_c_/*,lepton_m_,lepton_Vm*/),
                new cElementalDecay(0.9F, quark_u_, lepton_e_, lepton_Ve),
                eBosonDefinition.deadEnd);//makes photons and don't care
        quark_s_.init(quark_s, 0.6e-9F, 1, -1,
                new cElementalDecay(0.00163216F, quark_t_/*,lepton_t_,lepton_Vt*/),
                new cElementalDecay(0.050778116F, quark_u_, lepton_m_, lepton_Vm),
                new cElementalDecay(0.9F, quark_c_, lepton_e_, lepton_Ve),
                eBosonDefinition.deadEnd);//makes photons and don't care
        quark_b_.init(quark_b, 0.7e-13F, 0, -1,
                new cElementalDecay(1.23201e-5F, quark_u_, lepton_t_, lepton_Vt),
                new cElementalDecay(0.00169744F, quark_c_, lepton_m_, lepton_Vm),
                new cElementalDecay(0.9F, quark_t_, lepton_e_, lepton_Ve),
                eBosonDefinition.deadEnd);//makes photons and don't care
    }

    @Override
    public String getName() {
        return "Quark: " + name;
    }

    @Override
    public boolean isTimeSpanHalfLife() {
        return true;
    }
}
