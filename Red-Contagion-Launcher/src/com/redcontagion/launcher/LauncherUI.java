package com.redcontagion.launcher;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class LauncherUI extends JFrame implements ActionListener {

	private JPanel contentPane;
	private JList<File> lstVersions;

	public LauncherUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblVersions = new JLabel("Versions:");
		
		JScrollPane scrollPane = new JScrollPane();
		
		JButton btnPlay = new JButton("Play");
		btnPlay.setActionCommand("Play");
		btnPlay.addActionListener(this);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setActionCommand("Cancel");
		btnCancel.addActionListener(this);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(lblVersions)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(btnPlay)
							.addPreferredGap(ComponentPlacement.RELATED, 262, Short.MAX_VALUE)
							.addComponent(btnCancel))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(5)
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(lblVersions)
					.addGap(5)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 191, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnPlay)
						.addComponent(btnCancel))
					.addContainerGap(12, Short.MAX_VALUE))
		);
		
		lstVersions = new JList<File>(Launcher.getVersionFiles());
		lstVersions.setCellRenderer(new ListCellRenderer<File>() {

			@Override
			public Component getListCellRendererComponent(JList<? extends File> arg0, File arg1, int arg2, boolean arg3,
					boolean arg4) {
				return new JLabel(arg0.getName());
			}
			
		});
		scrollPane.setViewportView(lstVersions);
		contentPane.setLayout(gl_contentPane);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		switch(arg0.getActionCommand()) {
		case "Cancel":
			lstVersions.setSelectedIndex(-1);
			synchronized(this) {
				notifyAll();
			} break;
		case "Play":
			if (lstVersions.getSelectedValue() == null) return;
			synchronized(this) {
				notifyAll();
			} break;			
		}
	}
	
	public static File getVersion(String selected) {
		LauncherUI ui = new LauncherUI();
		final int length = ui.lstVersions.getMaxSelectionIndex() + 1;
		for(int i = 0; i < length; i++) {
			if (ui.lstVersions.getModel().getElementAt(i).getName().equals(selected))
				ui.lstVersions.setSelectedIndex(i);
		}
		ui.setVisible(true);
		ui.toFront();
		synchronized(ui) {
			try { ui.wait(); } catch (InterruptedException e) {}
		}
		ui.setVisible(false);
		ui.dispose();
		return ui.lstVersions.getSelectedValue();
	}
}
