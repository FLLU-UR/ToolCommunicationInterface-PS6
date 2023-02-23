package com.jbm.urcap.toolcommunicationinterface.impl;

import java.util.Locale;

import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.program.ContributionConfiguration;
import com.ur.urcap.api.contribution.program.CreationContext;
import com.ur.urcap.api.contribution.program.ProgramAPIProvider;
import com.ur.urcap.api.contribution.program.swing.SwingProgramNodeService;
import com.ur.urcap.api.domain.data.DataModel;

public class TCIProgramNodeService implements SwingProgramNodeService<TCIProgramNodeContribution, TCIProgramNodeView> {

	public TCIProgramNodeService() {
	}

	@Override
	public String getId() {
		return "TCI_Sample";
	}

	@Override
	public void configureContribution(ContributionConfiguration configuration) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getTitle(Locale locale) {
		return "TCI Sample";
	}

	@Override
	public TCIProgramNodeView createView(ViewAPIProvider apiProvider) {
		// TODO Auto-generated method stub
		Style style = new Style();
		return new TCIProgramNodeView(apiProvider, style);
	}

	@Override
	public TCIProgramNodeContribution createNode(ProgramAPIProvider apiProvider, TCIProgramNodeView view,
			DataModel model, CreationContext context) {
		// TODO Auto-generated method stub
		return new TCIProgramNodeContribution(apiProvider, view, model);
	}
	
}
