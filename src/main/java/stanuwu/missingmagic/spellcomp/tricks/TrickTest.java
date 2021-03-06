package stanuwu.missingmagic.spellcomp.tricks;

import stanuwu.missingmagic.Utils;
import stanuwu.missingmagic.spellcomp.Errors;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceTrick;

public class TrickTest extends PieceTrick {
    SpellParam<Number> input1;

    public TrickTest(Spell spell) {
        super(spell);
    }

    @Override
    public void initParams() {
        addParam(input1 = new ParamNumber(SpellParam.GENERIC_NAME_NUMBER, SpellParam.RED, false, false));
    }

    @Override
    public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
        super.addToMetadata(meta);
        double inputV = getParamEvaluationeOrDefault(input1, 1).doubleValue();
        if (inputV < 0) {
            Errors.test.compile(this.x, this.y);
        }
        meta.addStat(EnumSpellStat.POTENCY, (int) ((int)10*(inputV/100)));
        meta.addStat(EnumSpellStat.COST, (int) ((int) 100*(inputV/100)));
    }

    @Override
    public Object execute(SpellContext ctx) throws SpellRuntimeException {
        Utils.QMsg(ctx.caster, "Test: "+getNonnullParamValue(ctx, input1).doubleValue());
        return null;
    }
}
