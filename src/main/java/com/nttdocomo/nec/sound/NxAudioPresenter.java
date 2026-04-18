package com.nttdocomo.nec.sound;

import com.nttdocomo.ui.AudioPresenter;

/**
 * NEC handset wrapper around the base DoJa {@link AudioPresenter}.
 *
 * Binary-backed evidence for this subtype boundary:
 * - title bytecode references this exact type name and uses it only through
 *   inherited {@link AudioPresenter} members;
 * - the device dump contains the class name
 *   {@code com/nttdocomo/nec/sound/NxAudioPresenter} but no distinct
 *   presenter-specific member names or descriptors;
 * - title jars and the phone dump were both used as reference inputs, but the
 *   reconstructed surface is limited to the symbols they jointly prove.
 *
 * With no binary evidence of additional public members, the faithful runtime
 * reconstruction is a direct subtype shell over the standard presenter.
 */
public class NxAudioPresenter extends AudioPresenter {
    protected NxAudioPresenter() {
        super();
    }
}
