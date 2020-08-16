import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileSystemView;

public class Principal {

    static JFrame frame;
    static JPanel panelPrincipal, panelScroll, panelEstado, panelOpciones;
    static JScrollPane scrollPane;

    static JMenuBar menuBar;
    static JMenu menu1, menu2, menu3;
    static JMenuItem menu1item1, menuItem1, menuItem2, menuItem3;
    static JRadioButtonMenuItem rbMenuItem;
    static JCheckBoxMenuItem cbMenuItem;

    static Font fuente;
    static JTextField txtRuta;
    static JButton botonAnalizar;

    static JLabel statusLabel, opcionesLabel;
    
    static Directorio dir1 = null;
    
    static JRadioButton opcionPorcentaje;
    static JRadioButton opcionValAbs;
    static ButtonGroup grupoOpciones;
    
    static JCheckBox opcionCarpetas;
    
    static JComboBox comboFiltroTam;
    static JSlider sliderFiltroTamPorcentaje;
    static JTextField txtFiltroExt;
    static String strFiltroAux;
    
    static boolean soloCarpetas;
    static int tamMinimoAbs, tamMinimoRel;
    static String extension;

    public static void main(String[] args) throws InterruptedException {
        establecerLookAndFeel();
        inicializarVentana();
        inicializarPanelOpciones();
        inicializarPanelScroll();
        //analizarDirectorio();
        mostrarVentana();
        //TimeUnit.MILLISECONDS.sleep(750);
        //mostrarCargando();
    }

    private static void establecerLookAndFeel(){
        try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}
        catch (UnsupportedLookAndFeelException e) { }
        catch (ClassNotFoundException e) { }
        catch (InstantiationException e) { }
        catch (IllegalAccessException e) { }
    }

    private static void inicializarVentana(){
        frame = new JFrame ("Analizador de ficheros ~ Raúl Martínez");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1336, 768);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);	//Centra la ventana

        panelPrincipal = new JPanel();
        panelScroll = new JPanel();
        panelEstado = new JPanel();
        panelOpciones = new JPanel();

        panelPrincipal.setBorder(new BevelBorder(BevelBorder.RAISED));
        panelScroll.setBorder(new BevelBorder(BevelBorder.RAISED));
        panelEstado.setBorder(new BevelBorder(BevelBorder.LOWERED));
        panelOpciones.setBorder(new BevelBorder(BevelBorder.RAISED));

        frame.add(panelPrincipal, BorderLayout.CENTER);
        frame.add(panelEstado, BorderLayout.SOUTH);
        frame.add(panelOpciones, BorderLayout.EAST);

        menuBar = new JMenuBar();
        menu1 = new JMenu(" Guardar ");
        menu2 = new JMenu(" Ver ");
        menu3 = new JMenu(" Ayuda ");
        menuBar.add(menu1);
        menuBar.add(menu2);
        menuBar.add(menu3);

        menu1item1 = new JMenuItem("Generar informe");
        
        menuItem1 = new JMenuItem("Mostrar ayuda");
        menuItem2 = new JMenuItem("Acerca de...");
        menuItem3 = new JMenuItem("Web");
        menu1.add(menu1item1);
        menu3.add(menuItem1);
        menu3.add(menuItem2);
        menu2.add(menuItem3);
        
	    ActionListener al = new ActionListener() {
	  			public void actionPerformed(ActionEvent arg0) {
   				
	   				switch ( ((JMenuItem)arg0.getSource()).getLabel() ) {
	                    case "Generar informe":
							try {
								generarInforme();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	                        break;
	                    case "Mostrar ayuda":
	                    	JOptionPane.showMessageDialog(null, "Ayuda:\nPara comenzar haga clic en el botón Analizar. Use el panel derecho para filtrar o cambiar opciones.");
	                        break;
	                    case "Acerca de...":
	                    	JOptionPane.showMessageDialog(null, "Analizador de Ficheros:\nCreado por Raúl Martínez en 2019.");
	                        break;
	                    case "Web":
						try {
							java.awt.Desktop.getDesktop().browse(java.net.URI.create("https://rme.li"));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                        break;
	                    default:
	                    	JOptionPane.showMessageDialog(null, "Se ha elegido un menu desconocido.");
	   				}
	   				
	   			}
	 	};

 		menu1item1.addActionListener(al);
 		menuItem1.addActionListener(al);
 		menuItem2.addActionListener(al);
 		menuItem3.addActionListener(al);

        frame.setJMenuBar(menuBar);

        statusLabel = new JLabel("Estado actual: Correcto");

        panelEstado.add(statusLabel);
        

    }
    
    private static void inicializarPanelOpciones(){
    	
    	//Filtros por defecto
    	soloCarpetas = false;
    	tamMinimoAbs = 0;
    	tamMinimoRel = 0;
    	extension = "";
    	
    	opcionesLabel = new JLabel("Panel de Opciones");
    	fuente = new Font("Arial", Font.PLAIN, 18);
    	panelOpciones.setLayout(null);
    	panelOpciones.setPreferredSize(new Dimension(260, 500));
    	
    	opcionesLabel.setFont(fuente);    	
    	opcionesLabel.setBounds(54, -200, 260, 500);
    	
    	panelOpciones.add(opcionesLabel);
    	
        
    	
    	
        opcionPorcentaje = new JRadioButton("Porcentaje");
        opcionValAbs = new JRadioButton("Valor absoluto");
        opcionPorcentaje.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(opcionPorcentaje.isSelected()) {
					//JOptionPane.showMessageDialog(null, "valor absoluto");
		            for ( Component child : ( ( Container ) panelScroll ).getComponents () )
		            {
		            	if (child instanceof JPanel) {
		            		for ( Component c : ( ( JPanel ) child ).getComponents () )
		    	            {
		            			if (c instanceof JLabel) {
		            				if( ((JLabel)c).getText()!=null  && ((JLabel)c).getText().indexOf("Tamaño: ") != -1) {
			            				((JLabel)c).setVisible(false);
		            				}else if( ((JLabel)c).getText()!=null  && ((JLabel)c).getText().indexOf("Porcentaje: ") != -1) {
			            				((JLabel)c).setVisible(true);
		            				}
		            			}
		    	            }
		                 }
		            }
				}
			}
		});
        
        opcionValAbs.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(opcionValAbs.isSelected()) {
		            //JOptionPane.showMessageDialog(null, "valor absoluto");
		            for ( Component child : ( ( Container ) panelScroll ).getComponents () )
		            {
		            	if (child instanceof JPanel) {
		            		for ( Component c : ( ( JPanel ) child ).getComponents () )
		    	            {
		            			if (c instanceof JLabel) {
		            				if( ((JLabel)c).getText()!=null  && ((JLabel)c).getText().indexOf("Tamaño: ") != -1) {
			            				((JLabel)c).setVisible(true);
		            				}else if( ((JLabel)c).getText()!=null  && ((JLabel)c).getText().indexOf("Porcentaje: ") != -1) {
			            				((JLabel)c).setVisible(false);
		            				}
		            			}
		    	            }
		                 }
		            }
				}
			}
		});
        
        grupoOpciones = new ButtonGroup();
        grupoOpciones.add(opcionPorcentaje);
        grupoOpciones.add(opcionValAbs);
        opcionValAbs.setSelected(true);
    	
        JPanel panelOpcionesTamano = new JPanel();
        panelOpcionesTamano.setLayout(new BoxLayout(panelOpcionesTamano, BoxLayout.PAGE_AXIS));
        TitledBorder border = new TitledBorder("Mostrar tamaño en");

        border.setTitleJustification(TitledBorder.CENTER);
        border.setTitlePosition(TitledBorder.TOP);
        panelOpcionesTamano.setBorder(border);     
        panelOpcionesTamano.add(opcionPorcentaje);
        panelOpcionesTamano.add(opcionValAbs);
        
        panelOpcionesTamano.setBounds(26, 100, 200, 70);
        panelOpciones.add(panelOpcionesTamano);
        
        
        
        //
        
        opcionCarpetas = new JCheckBox("Solo carpetas");
        opcionCarpetas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				filtrar(opcionCarpetas.isSelected(), tamMinimoAbs, tamMinimoRel, extension);
			}
		});
        
        JPanel panelOpcionesCarpeta = new JPanel();
        panelOpcionesCarpeta.setLayout(new BoxLayout(panelOpcionesCarpeta, BoxLayout.PAGE_AXIS));
        TitledBorder borderCarpeta = new TitledBorder("Mostrar");
        borderCarpeta.setTitleJustification(TitledBorder.CENTER);
        borderCarpeta.setTitlePosition(TitledBorder.TOP);
        panelOpcionesCarpeta.setBorder(borderCarpeta);
        panelOpcionesCarpeta.add(opcionCarpetas);
        
        panelOpcionesCarpeta.setBounds(26, 200, 200, 70);
        panelOpciones.add(panelOpcionesCarpeta);
             
        //
        
        String[] listaTams = { " Sin tamaño mínimo", " Minúsculo (>8 KB)", " Pequeño (>256 KB)", " Mediano (>8 MB)", " Grande (>256 MB)", " Gigante (>1 GB)" };
        comboFiltroTam = new JComboBox(listaTams);
        comboFiltroTam.setSelectedIndex(0);
        
        comboFiltroTam.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				filtrar(soloCarpetas, ((JComboBox)e.getSource()).getSelectedIndex(), tamMinimoRel, extension);
			}
		});
        
        JPanel panelOpcionesFiltroTam = new JPanel();
        panelOpcionesFiltroTam.setLayout(new BoxLayout(panelOpcionesFiltroTam, BoxLayout.PAGE_AXIS));
        TitledBorder borderFiltroTam = new TitledBorder("Tamaño mínimo absoluto");
        borderFiltroTam.setTitleJustification(TitledBorder.CENTER);
        borderFiltroTam.setTitlePosition(TitledBorder.TOP);
        panelOpcionesFiltroTam.setBorder(borderFiltroTam);
        panelOpcionesFiltroTam.add(comboFiltroTam);
        
        panelOpcionesFiltroTam.setBounds(26, 300, 200, 70);
        panelOpciones.add(panelOpcionesFiltroTam);
        
        
        //
        sliderFiltroTamPorcentaje = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        sliderFiltroTamPorcentaje.setSnapToTicks(true);
        sliderFiltroTamPorcentaje.setMajorTickSpacing(10);
        sliderFiltroTamPorcentaje.setMinorTickSpacing(5);
        sliderFiltroTamPorcentaje.setPaintTicks(true);
        sliderFiltroTamPorcentaje.setPaintLabels(true);
        sliderFiltroTamPorcentaje.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(sliderFiltroTamPorcentaje.getValue()%5 == 0) {	//Cuando para de mover el slider siempre cae en multiplo de 5
			        filtrar(soloCarpetas, tamMinimoAbs, sliderFiltroTamPorcentaje.getValue(), extension);
				}
			}
		});
        
        JPanel panelOpcionesFiltroTamPorcentaje = new JPanel();
        panelOpcionesFiltroTamPorcentaje.setLayout(new BoxLayout(panelOpcionesFiltroTamPorcentaje, BoxLayout.PAGE_AXIS));
        TitledBorder borderFiltroTamPorcentaje = new TitledBorder("Tamaño mínimo en %");
        borderFiltroTamPorcentaje.setTitleJustification(TitledBorder.CENTER);
        borderFiltroTamPorcentaje.setTitlePosition(TitledBorder.TOP);
        panelOpcionesFiltroTamPorcentaje.setBorder(borderFiltroTamPorcentaje);   
        panelOpcionesFiltroTamPorcentaje.add(sliderFiltroTamPorcentaje);
        
        panelOpcionesFiltroTamPorcentaje.setBounds(26, 400, 200, 70);
        panelOpciones.add(panelOpcionesFiltroTamPorcentaje);
        
        //
        txtFiltroExt = new JTextField();
        JLabel labelFiltroExt = new JLabel("Extensión");
        txtFiltroExt.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				strFiltroAux = txtFiltroExt.getText();
				
				if( strFiltroAux.equals(" ") || strFiltroAux.equals("  ") ) {
					txtFiltroExt.setText("");
					strFiltroAux = "";
				}else if( strFiltroAux.length()>1 && strFiltroAux.substring(0, 1).equals(".")  ) {
					txtFiltroExt.setText( strFiltroAux.substring(1) );
					strFiltroAux = strFiltroAux.substring(1);
				}
				
				filtrar(soloCarpetas, tamMinimoAbs, tamMinimoRel, strFiltroAux);
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
       
        JPanel panelOpcionesFiltroExt = new JPanel();
        panelOpcionesFiltroExt.setLayout(new BoxLayout(panelOpcionesFiltroExt, BoxLayout.PAGE_AXIS));
        TitledBorder borderFiltroExt = new TitledBorder("Filtrar por extensión");
        borderFiltroExt.setTitleJustification(TitledBorder.CENTER);
        borderFiltroExt.setTitlePosition(TitledBorder.TOP);
        panelOpcionesFiltroExt.setBorder(borderFiltroExt);
        panelOpcionesFiltroExt.add(labelFiltroExt);
        panelOpcionesFiltroExt.add(txtFiltroExt);
        
        panelOpcionesFiltroExt.setBounds(26, 500, 200, 70);
        panelOpciones.add(panelOpcionesFiltroExt);
        
        
        //
        JButton botonRestablecerOpciones = new JButton("Reestablecer opciones");
        botonRestablecerOpciones.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				grupoOpciones.clearSelection();
				opcionPorcentaje.setSelected(false);
				opcionValAbs.setSelected(true);
				opcionCarpetas.setSelected(false);
				comboFiltroTam.setSelectedIndex(0);
				sliderFiltroTamPorcentaje.setValue(0);
				txtFiltroExt.setText("");
		
				filtrar(false, 0, 0, "");		    	//Filtros por defecto
			}
		});
        botonRestablecerOpciones.setBounds(26, 600, 200, 35);
        panelOpciones.add(botonRestablecerOpciones);
             
    	
    }
    private static void inicializarPanelScroll(){
    	   	
        fuente = new Font("Arial", Font.PLAIN, 18);
        txtRuta = new JTextField("C:\\", 64);
        txtRuta.setToolTipText("Introduce una ruta y pulsa [ENTER]");
        botonAnalizar = new JButton("Analizar");
        txtRuta.setFont(fuente);
        botonAnalizar.setFont(fuente);

        botonAnalizar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int rVal = fc.showOpenDialog(frame);

                if(rVal == JFileChooser.APPROVE_OPTION) {
                    txtRuta.setText(fc.getSelectedFile().getPath());
                    analizarDirectorio(fc.getSelectedFile().getPath());
                }else if(rVal == JFileChooser.CANCEL_OPTION) {
                    txtRuta.setText("C:\\");
                }

            }
        });
        
        txtRuta.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	analizarDirectorio(txtRuta.getText());
              }
            });

        panelPrincipal.add(txtRuta);
        panelPrincipal.add(botonAnalizar);

        scrollPane = new JScrollPane(panelScroll);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(1000, 600));
        
        mostrarInformacion();
        
    }
    
    private static void mostrarCargando() {
        resetearPanelScroll();
        
        JPanel auxPanel = new JPanel();
        auxPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
        auxPanel.setLayout( null );
        auxPanel.setPreferredSize(new Dimension(940, 120));
        

        
        panelScroll.add(auxPanel);
        
        panelPrincipal.add(scrollPane);


        statusLabel.setText("Cargando...");
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setString("Cargando...");
        progressBar.setStringPainted(true);
        progressBar.setBounds(220, 40, 500, 30);
        
        auxPanel.add(progressBar);
       
        
                 try {
			TimeUnit.MILLISECONDS.sleep(750);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    
    private static void mostrarInformacion() {
        resetearPanelScroll();
        
        JPanel auxPanel = new JPanel();
        auxPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
        auxPanel.setLayout( new FlowLayout() );
        auxPanel.setPreferredSize(new Dimension(940, 120));
        
        fuente = new Font("Arial", Font.PLAIN, 24);

        
        JLabel label = new JLabel( "Bienvenido, haz clic en Analizar para comenzar" );
        label.setFont(fuente);
        label.setPreferredSize(new Dimension(600, 100));
        auxPanel.add(label);
        
        panelScroll.add(auxPanel);
        
        panelPrincipal.add(scrollPane);

        statusLabel.setText("Haz clic en Analizar para comenzar");
    }

    private static void analizarDirectorio(String ruta) {
        mostrarCargando();
        resetearPanelScroll();
        	System.out.println("Analizando...");
        dir1 = new Directorio(new File(ruta));
        	System.out.println("Analizando... (2)");
        dir1.inicializarContenido();
        	System.out.println("Analizando... (3)");
        dir1.ordenarSegunTamano();
        	System.out.println("Pintando...");

        int i = 0;
        Iterator<File> iter = dir1.contenido.iterator();
        while (iter.hasNext() && i < 10000) { //limite de 10000 archivos mostrados en la UI
            File ficheroAux = iter.next();

            //System.out.println( Directorio.obtenerExtension(iter.next()) );


            JPanel auxPanel = new JPanel();
            auxPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
            auxPanel.setLayout( new FlowLayout() );
            auxPanel.setPreferredSize(new Dimension(940, 40));

            String tipoFicheroAux;
            if(ficheroAux.isDirectory()) tipoFicheroAux = "directorio";
            else {
                switch (Directorio.obtenerExtension(ficheroAux)) {
                    case "pdf":
                        tipoFicheroAux = "pdf";
                        break;
                    case "doc":
                    case "docx":
                        tipoFicheroAux = "doc";
                        break;
                    case "jpg":
                    case "jpeg":
                    case "gif":
                    case "png":
                    case "ico":
                        tipoFicheroAux = "jpg";
                        break;
                    default:
                        tipoFicheroAux = "otro";
                }
            }
            
            BufferedImage picAux = null;
            try {picAux = ImageIO.read(new File("src/"+tipoFicheroAux+".png"));} catch (IOException e) {e.printStackTrace();}
            JLabel iconoAux = new JLabel(new ImageIcon(picAux));
            auxPanel.add(iconoAux);
			
            JLabel label = new JLabel( ficheroAux.getName() );
            label.setPreferredSize(new Dimension(230, 16+1));
            auxPanel.add(label);


            JProgressBar progressBar = new JProgressBar(0, 10000);

            long tamFicheroAux;

            
            if (ficheroAux.isDirectory()) {
                tamFicheroAux = Directorio.obtenerTamano(ficheroAux);
                JLabel labelAuxEsDirectorio = new JLabel("enEfectoSoyUnDirectorio");
                labelAuxEsDirectorio.setVisible(false); //Usado como variable para filtrar   
                auxPanel.add(labelAuxEsDirectorio);
                
                JLabel labelAuxParaInforme = new JLabel("laInformacionParaElInformeEs" + "DIRECTORIO> " + ficheroAux.getAbsolutePath() + " | TAMAÑO: " + Directorio.unidadesLegibles( tamFicheroAux ) );
                labelAuxParaInforme.setVisible(false); //Usado como variable para filtrar   
                auxPanel.add(labelAuxParaInforme);
            }else {
                tamFicheroAux = ficheroAux.length();
                JLabel labelAuxEsArchivo = new JLabel("enEfectoSoyUnArchivo");
                labelAuxEsArchivo.setVisible(false); //Usado como variable para filtrar
                auxPanel.add(labelAuxEsArchivo);
                
                JLabel labelAuxParaInforme = new JLabel("laInformacionParaElInformeEs" + "FICHERO> " + ficheroAux.getAbsolutePath() + " | TAMAÑO: " + Directorio.unidadesLegibles( tamFicheroAux ) );
                labelAuxParaInforme.setVisible(false); //Usado como variable para filtrar   
                auxPanel.add(labelAuxParaInforme);
                
                /*
                if(opcionCarpetas.isSelected()) {
                	auxPanel.setVisible(false);
                }
                */
            }

            JLabel labelAuxTamAbsoluto = new JLabel("elTamAbsolutoEs" + tamFicheroAux);
            labelAuxTamAbsoluto.setVisible(false); //Usado como variable para filtrar por tamaño
            auxPanel.add(labelAuxTamAbsoluto);
            
            String extensionAux = Directorio.obtenerExtension(ficheroAux);
            if(extensionAux.equals("")) extensionAux = " ";
            JLabel labelAuxExtension = new JLabel("laExtensionEs" + extensionAux);
            labelAuxExtension.setVisible(false); //Usado como variable para filtrar por extension
            auxPanel.add(labelAuxExtension);

            JLabel label2 = new JLabel("Tamaño: " + Directorio.unidadesLegibles( tamFicheroAux ) );
            label2.setPreferredSize(new Dimension(125, 16+1));
            auxPanel.add(label2);
            
            JLabel label3 = new JLabel("Porcentaje: " +  new DecimalFormat("#.##").format( (double)tamFicheroAux/(double)dir1.tamRaiz*100 )  + "%" );
            label3.setPreferredSize(new Dimension(125, 16+1));
            auxPanel.add(label3);
            
            
            if(opcionValAbs.isSelected()){
                label2.setVisible(true);
                label3.setVisible(false);
            }else {
                label2.setVisible(false);
                label3.setVisible(true);
            }
            
            /*
            long tamMinimo = 0; //bytes
			switch( comboFiltroTam.getSelectedIndex() ) {
			  case 0:
				  tamMinimo = 0;
			    break;
			  case 1:
				  tamMinimo = 8192;
			    break;
			  case 2:
				  tamMinimo = 262144;
			    break;
			  case 3:
				  tamMinimo = 8388608;
			    break;
			  case 4:
				  tamMinimo = 268435456;
			    break;
			  case 5:
				  tamMinimo = 1073741824;
			    break;
			}
			if(tamFicheroAux < tamMinimo) {
				auxPanel.setVisible(false);
			}

			strFiltroAux = txtFiltroExt.getText();
			if( !strFiltroAux.equals("") && !strFiltroAux.equalsIgnoreCase(extensionAux) ) {
				auxPanel.setVisible(false);
			}
			*/

            progressBar.setValue( (int)( (double)tamFicheroAux/(double)dir1.tamRaiz*10000 ));
            progressBar.setPreferredSize( new Dimension (250, 16+1) );
            progressBar.setStringPainted(true);
            progressBar.setString(" ");
            auxPanel.add(progressBar);

            ActionListener alBotonesFichero = new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
    				if( ((JButton)e.getSource()).getText() == "Ver" ) {
    					try {
    						Desktop.getDesktop().open(ficheroAux);
    					} catch (IOException e1) {
    						// TODO Auto-generated catch block
    						e1.printStackTrace();
    					}
    				}else if( ((JButton)e.getSource()).getText() == "Borrar" ) {
    					JOptionPane.showMessageDialog(null, "Por motivos de seguridad no se ha implementado la opción de borrar ficheros o directorios.");
    				}else if( ((JButton)e.getSource()).getText() == "Información" ) {
    					String mensajeAux = "Información del fichero:\n"
    							+ "\nNombre: " + ficheroAux.getName()
    							+ "\nRuta: " + ficheroAux.getAbsolutePath()
    							+ "\nTamaño: " + ficheroAux.length()
    							+ "\nÚltima modificación: " + new Date(ficheroAux.lastModified());
    					JOptionPane.showMessageDialog(null, mensajeAux);
    				}
    			}
            };
            
            JButton buton1 = new JButton("Ver");
            buton1.addActionListener(alBotonesFichero);
            auxPanel.add(buton1);

            JButton buton2 = new JButton("Borrar");
            buton2.addActionListener(alBotonesFichero);
            auxPanel.add(buton2);

            JButton buton3 = new JButton("Información");
            buton3.addActionListener(alBotonesFichero);
            auxPanel.add(buton3);
            
            panelScroll.add(auxPanel);
            i++;
            
        }
        
        filtrar(soloCarpetas, tamMinimoAbs, tamMinimoRel, extension);
        
        if(dir1.contenido.size() == 0){
            JOptionPane.showMessageDialog(null, "Has elegido un directorio sin archivos.");
        }else if(dir1.contenido.size() >= 2500 && dir1.contenido.size() < 10000){
            JOptionPane.showMessageDialog(null, "Has elegido un directorio con bastantes archivos, puede que no se muestren ordenados.");
            statusLabel.setText("Elementos mostrados: " +  dir1.contenido.size() + " Tamaño directorio raiz: "
                    + Directorio.unidadesLegibles(dir1.tamRaiz) );
        }else if(dir1.contenido.size() >= 10000){
            JOptionPane.showMessageDialog(null, "Has elegido un directorio con bastantes archivos, solo se mostrarán los 10000 primeros y puede que no se muestren ordenados.");
            statusLabel.setText("Elementos mostrados: " + 10000 + " Tamaño directorio raiz: "
                    + Directorio.unidadesLegibles(dir1.tamRaiz) );
        }else{
            statusLabel.setText("Elementos mostrados: " + dir1.contenido.size() + " Tamaño directorio raiz: "
                    + Directorio.unidadesLegibles(dir1.tamRaiz) );
        }
        recalcularAlturaPanelScroll();
        panelPrincipal.add(scrollPane);

        mostrarVentana();
    }

    private static void generarInforme() throws IOException {
    	if(dir1==null) {
    		JOptionPane.showMessageDialog(null, "Para generar un informe debes de analizar primero un directorio.");
    	}else{
    		
    		PrintWriter fOut = new PrintWriter(new BufferedWriter(new FileWriter(dir1.fileRaiz.getAbsolutePath()+"/informe.txt", false)));
            
    		fOut.println("# INFORME #");
    		fOut.println("# DE: \"" + dir1.fileRaiz.getAbsolutePath() + "\" #");
    		
        	for ( Component child : ( ( Container ) panelScroll ).getComponents () )
            {
            	if (child instanceof JPanel && child.isVisible()) {
	            		for ( Component c : ( ( JPanel ) child ).getComponents () )
	    	            {
	            			if (c instanceof JLabel) {
	            				if( ((JLabel)c).getText()!=null  && ((JLabel)c).getText().indexOf("laInformacionParaElInformeEs") != -1) {
	            					 fOut.println(((JLabel)c).getText().substring(28));	            					
	            				}
	            			}
	    	            }

                 }
            }

            
            fOut.println("# FIN #");
            fOut.close();
    		JOptionPane.showMessageDialog(null, "Se ha generado el informe. Recuerda que los filtros activos afectan también al informe. \n Se ha guardado en " + dir1.fileRaiz.getAbsolutePath()+"\\informe.txt");
    	}
    }
    
    private static void generarInformeOLD() throws IOException {
    	if(dir1==null) {
    		JOptionPane.showMessageDialog(null, "Para generar un informe debes de analizar primero un directorio.");
    	}else{
    		
    		PrintWriter fOut = new PrintWriter(new BufferedWriter(new FileWriter("informe.txt", false)));
            
    		fOut.println("# INFORME #");
    		int i = 0;
            Iterator<File> iter = dir1.contenido.iterator();
            while (iter.hasNext() && i < 10000) { //limite de 10000 archivos mostrados en la UI
                File ficheroAux = iter.next();
                if(ficheroAux.isDirectory()) {
                	 fOut.println("DIRECTORIO > "+ficheroAux.getAbsolutePath());
                }else {
                	fOut.println("ARCHIVO > "+ficheroAux.getAbsolutePath());
                }
               
            }
            fOut.println("# FIN #");
            fOut.close();
    		JOptionPane.showMessageDialog(null, "Se ha generado el informe.");
    	}
    }
    
    private static void mostrarVentana(){
        frame.setVisible(true);
    }
    
    private static void recalcularAlturaPanelScroll(){
    	
    	int i = 0;
    	for ( Component child : ( ( Container ) panelScroll ).getComponents () )
        {
        	if (child instanceof JPanel && child.isVisible()) {
        		i++;
             }
        }
    	
        panelScroll.setPreferredSize(new Dimension(960, 45 * i + 15));
        statusLabel.setText("Elementos mostrados: " + i);
        
    }


    private static void resetearPanelScroll(){
        panelScroll.removeAll();
       
        scrollPane.repaint();
    }

    private static void filtrar(boolean _soloCarpetas, int _tamMinimoAbs, int _tamMinimoRel, String _extension){
    	

        	//Todo visible y luego vamos ocultando
        	for ( Component child : ( ( Container ) panelScroll ).getComponents () )
            {
            	if (child instanceof JPanel) {
            		for ( Component c : ( ( JPanel ) child ).getComponents () )
    	            {
            			if (c instanceof JProgressBar) {
                			((JProgressBar)c).getParent().setVisible(true);
            			}
    	            }
                 }
            }
        	
        	
        	soloCarpetas = _soloCarpetas;
        	if(soloCarpetas) {
            	for ( Component child : ( ( Container ) panelScroll ).getComponents () )
                {
                	if (child instanceof JPanel) {
                		for ( Component c : ( ( JPanel ) child ).getComponents () )
        	            {
                			if (c instanceof JLabel) {
                				if( ((JLabel)c).getText()!=null  && ((JLabel)c).getText().indexOf("enEfectoSoyUnArchivo") != -1) {
    	            				((JLabel)c).getParent().setVisible(false);
                				}
                			}
        	            }
                     }
                }
        	}

        	tamMinimoAbs = _tamMinimoAbs;
        	if(tamMinimoAbs != 0) {
        		long tamMinimo = 0; //bytes
    			switch( tamMinimoAbs ) {
    			  case 0:
    				  tamMinimo = 0;
    			    break;
    			  case 1:
    				  tamMinimo = 8192;
    			    break;
    			  case 2:
    				  tamMinimo = 262144;
    			    break;
    			  case 3:
    				  tamMinimo = 8388608;
    			    break;
    			  case 4:
    				  tamMinimo = 268435456;
    			    break;
    			  case 5:
    				  tamMinimo = 1073741824;
    			    break;
    			}

    				for ( Component child : ( ( Container ) panelScroll ).getComponents () )
    	            {
    	            	if (child instanceof JPanel) {
    	            		for ( Component c : ( ( JPanel ) child ).getComponents () )
    	    	            {
    	            			if (c instanceof JLabel) {
    	            				if( ((JLabel)c).getText()!=null  && ((JLabel)c).getText().indexOf("elTamAbsolutoEs") != -1) {
    		            				if(Long.parseLong( ((JLabel)c).getText().substring(15) ) < tamMinimo) {
    		            					((JLabel)c).getParent().setVisible(false);
    		            				}
    	            				}
    	            			}
    	    	            }
    	                 }
    	            }
        	}
        	
        	
        	tamMinimoRel = _tamMinimoRel;
        	if(tamMinimoRel != 0) {
				for ( Component child : ( ( Container ) panelScroll ).getComponents () )
	            {
	            	if (child instanceof JPanel) {
	            		for ( Component c : ( ( JPanel ) child ).getComponents () )
	    	            {
	            			if (c instanceof JProgressBar) {

	            				if(((JProgressBar)c).getValue() < tamMinimoRel*100) {
	            					((JProgressBar)c).getParent().setVisible(false);
	            				}
	            				
	            			}
	    	            }
	                 }
	            }
        	}
			
        	
        	extension = _extension;
        	if(!extension.equalsIgnoreCase("")) {
        		for ( Component child : ( ( Container ) panelScroll ).getComponents () )
	            {
	            	if (child instanceof JPanel) {
	            		for ( Component c : ( ( JPanel ) child ).getComponents () )
	    	            {
	            			if (c instanceof JLabel) {
	            				if( ((JLabel)c).getText()!=null  && ((JLabel)c).getText().indexOf("laExtensionEs") != -1) {
		            				if( !((JLabel)c).getText().substring(13).equalsIgnoreCase(strFiltroAux) ) {
		            					((JLabel)c).getParent().setVisible(false);
		            				}
	            				}
	            			}
	    	            }
	                 }
	            }
        	}
        	
                
		recalcularAlturaPanelScroll();
		
    }

}
