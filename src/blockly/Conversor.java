package blockly;

import java.awt.*;

public class Conversor extends javax.swing.JFrame {
    
    public Conversor() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents

    private void initComponents() {
        jScrollPane1 = new javax.swing.JScrollPane();
        area_fonte = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        area_resultado = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        area_fonte.setColumns(20);
        area_fonte.setRows(5);
        jScrollPane1.setViewportView(area_fonte);

        area_resultado.setEditable(false);
        area_resultado.setColumns(20);
        area_resultado.setRows(5);
        jScrollPane2.setViewportView(area_resultado);

        jButton1.setText("Converter");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("-------------->");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(187, 187, 187)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton1ActionPerformed
    {
        //GEN-HEADEREND:event_jButton1ActionPerformed
        String frente = "", tras = "", direita = "", esquerda = "", funcao = "", velocidade = "", modo = "", tempo = "";
        String texto_final = "", variaveis = "";
        
        /***********************************************************************************************/
        
        // Essa parte verifica se existe a função dos motores, e também salva
        // temporariamente na variavel texto_final as variaveis de direção para
        // a próxima parte do código
        
        boolean jump = false;
        for (String linha : area_fonte.getText().split("\\n")) {
            if (jump) {
                if (linha.matches("end")) jump = false;
                continue;
            }
            else if (linha.matches("(\\s*)|(--.*)|end|(\\w+\\(.*\\))")) {
                continue;
            }
            else if (linha.matches("function .*")) {
                jump = true;
                String[] palavras = linha.split(" ");
                if (!(palavras[1].matches("loop\\(\\)"))) funcao = palavras[1].substring(0, palavras[1].length() - 2);
            }
            else {
                variaveis += linha + "\n";
            }
        }
        if (funcao.isEmpty()) {
            erro("Não há a função dos motores que\nserá chamada durante o loop");
            return;
        }
        
        /***********************************************************************************************/
        
        // Essa parte verifica os valores das variáveis de direção e salva seus
        // nomes para usá-los depois, durante a função loop, e retorna erro caso
        // esteja faltando uma das 4 direções (1, 2, 3, 4)
        
        for (String linha : variaveis.split("\\n")) {
            String[] palavras = linha.split(" ");
            if      (palavras[2].matches("1")) frente   = palavras[0];
            else if (palavras[2].matches("2")) tras     = palavras[0];
            else if (palavras[2].matches("3")) direita  = palavras[0];
            else if (palavras[2].matches("4")) esquerda = palavras[0];
        }
        if (frente.isEmpty() || tras.isEmpty() || direita.isEmpty() || esquerda.isEmpty()) {
            erro("Falta variáveis de direção");
            return;
        }
        
        /***********************************************************************************************/
        
        // Aqui ocorre o restante, que é interpretar a função loop e transcrever
        // seu código para a função loop do arduino, usando os dados salvos
        // anteriormente
        // Retorna erro se não existe a função loop, se foi chamado a função dos
        // motores sem ter preenchido as 3 variaveis (velocidade, direção, tempo),
        // se alguma linha tiver uma sintáxe errada
        
        int val1 = 0; String val2 = ""; int val3 = 0;
        boolean sem_loop = true;
        for (String linha : area_fonte.getText().split("\\n")) {
            if (sem_loop) {
                if (linha.matches("function loop\\(\\)")) sem_loop = false;
            }
            else if (linha.matches("end"))  break;
            else {
                if (linha.matches(".*\\(\\)"))  {
                    if (linha.matches("\\s*" + funcao + "\\(\\)")) {
                        if (velocidade.isEmpty() || modo.isEmpty() || tempo.isEmpty()) {
                            erro("Uma das variáveis Velocidade|Modo|Tempo não recebeu valor");
                            return;
                        }
                        else {
                            int direcao = 0;
                            if      (val2.matches(frente))   direcao = 1;
                            else if (val2.matches(tras))     direcao = 2;
                            else if (val2.matches(direita))  direcao = 3;
                            else if (val2.matches(esquerda)) direcao = 4;
                            texto_final += "\nmotores" + "(" + val1 + ", " + direcao + ", " + val3 + ")";
                        }
                    }
                    else {
                        erro("Não existe função com esse nome\nTalvez você quis dizer " + funcao);
                        return;
                    }
                }
                else {
                    String[] palavras = linha.trim().split("\\s+");
                    if (palavras.length != 3) {
                        erro("Erro de sintáxe");
                        return;
                    }
                    else {
                        if      (velocidade.isEmpty())  velocidade  = palavras[0];
                        else if (modo.isEmpty())        modo        = palavras[0];
                        else if (tempo.isEmpty())       tempo       = palavras[0];
                        if      (palavras[0].matches(velocidade))   val1 = Integer.parseInt(palavras[2]);
                        else if (palavras[0].matches(modo))         val2 = palavras[2];
                        else if (palavras[0].matches(tempo))        val3 = Integer.parseInt(palavras[2]);
                    }
                }
            }
        }
        if (sem_loop) {
            erro("O codigo nao contem a funçao loop()");
            return;
        }
        
        /***********************************************************************************************/
        
        printar(texto_final);
        
    }//GEN-LAST:event_jButton1ActionPerformed

    public void printar(String texto) {
        texto = "// Bibliotecas (funcoes prefeitas)\n" +
        "#include <Servo.h> //Biblioteca de funcoes prefeitas que permitem movimentar o servomotor\n" +
        "\n" +
        "// Variaveis (valores que podem ser alterados ao longo do programa)\n" +
        "\n" +
        "int EnableDir1 = 7;    // Input do driver de corrente - Definicao do sentido de rotacao do motor associada ao pino 7\n" +
        "int EnableDir2 = 5;    // Input do driver de corrente - Definicao do sentido de rotacao do motor associada ao pino 5\n" +
        "int VelMotDir = 6;     // Enabler do motor - Associacao do pino 6 a velocidade atribuida aos motores da direita\n" +
        "int EnableEsq1 = 4;    // Input do driver de corrente - Definicao do sentido de rotacao do motor associada ao pino 4\n" +
        "int EnableEsq2 = 2;    // Input do driver de corrente - Definicao do sentido de rotacao do motor associada ao pino 2\n" +
        "int VelMotEsq = 3;     // Enabler do motor - Associacao do pino 3 a velocidade atribuida aos motores da esquerda\n" +
        "\n" +
        "Ultrasonic sensorUS(12,13);  // Objeto que representa o sensor ultrasom\n" +
        "int obstaculo;               // Variavel onde  guardada a distancia obtida pelo ultrasom\n" +
        "\n" +
        "Servo servomotor;  // Objeto que representa o servomotor\n" +
        "int posicao = 90;    // Variavel que define a posicao inicial do servomotor\n" +
        "\n" +
        "// Funcoes (realizacao de uma tarefa predeterminada)\n" +
        "\n" +
        "void motores(int vel, int modo, int tempo) // Funcao para fazer movimentar os motores\n" +
        "{\n" +
        "  // Para utilizar a funcao e necessario estabelecer 3 definicoes:\n" +
        "  // ---Variavel vel - Velocidade dos motores\n" +
        "  // ---Variavel modo - Como se vai movimentar o robo (modo = 1 andar em frente, modo = 2 andar para tras, modo = 3 virar a direita, modo = 4 virar a esquerda)\n" +
        "  // ---Variavem tempo - quanto tempo o robo vira NOTA: Se o movimento for para a frente ou para tras o valor do tempo deve ser colocado a zero\n" +
        "  switch(modo)\n" +
        "  {\n" +
        "    // Andar para a frente - modo = 1\n" +
        "    case 1:\n" +
        "      analogWrite (VelMotDir, vel); // Velocidade motor direita\n" +
        "      analogWrite (VelMotEsq, vel); // Velocidade motor esquerda\n" +
        "      digitalWrite (EnableDir1, LOW);\n" +
        "      digitalWrite (EnableDir2, HIGH);\n" +
        "      digitalWrite (EnableEsq1, HIGH);\n" +
        "      digitalWrite (EnableEsq2, LOW);\n" +
        "      delay(tempo*1000);\n" +
        "    break;\n" +
        "    // Andar para tras - modo = 2\n" +
        "    case 2:\n" +
        "      analogWrite (VelMotDir, vel); // Velocidade motor direita\n" +
        "      analogWrite (VelMotEsq, vel); // Velocidade motor esquerda\n" +
        "      digitalWrite (EnableDir1, HIGH);\n" +
        "      digitalWrite (EnableDir2, LOW);\n" +
        "      digitalWrite (EnableEsq1, LOW);\n" +
        "      digitalWrite (EnableEsq2, HIGH);\n" +
        "      delay(tempo*1000);\n" +
        "    break;\n" +
        "    // Virar a direita - modo = 3\n" +
        "    case 3:\n" +
        "      analogWrite (VelMotDir, vel); // Velocidade motor direita\n" +
        "      analogWrite (VelMotEsq, vel); // Velocidade motor esquerda\n" +
        "      digitalWrite (EnableDir1, HIGH);\n" +
        "      digitalWrite (EnableDir2, LOW);\n" +
        "      digitalWrite (EnableEsq1, HIGH);\n" +
        "      digitalWrite (EnableEsq2, LOW);\n" +
        "      delay(tempo*1000);\n" +
        "    break;\n" +
        "    // Virar a esquerda - modo = 4\n" +
        "    case 4:\n" +
        "      analogWrite (VelMotDir, vel); // Velocidade motor direita\n" +
        "      analogWrite (VelMotEsq, vel); // Velocidade motor esquerda\n" +
        "      digitalWrite (EnableDir1, LOW);\n" +
        "      digitalWrite (EnableDir2, HIGH);\n" +
        "      digitalWrite (EnableEsq1, LOW);\n" +
        "      digitalWrite (EnableEsq2, HIGH);\n" +
        "      delay(tempo*1000);\n" +
        "    break;\n" +
        "  }\n" +
        "}\n" +
        "// Void Setup (definicoes dos pinos)\n" +
        "\n" +
        "void setup ()\n" +
        "{\n" +
        "  Serial.begin(9600);\n" +
        "  \n" +
        "  pinMode (EnableDir1, OUTPUT);\n" +
        "  pinMode (EnableDir2, OUTPUT);\n" +
        "  pinMode (VelMotDir, OUTPUT);\n" +
        "  pinMode (EnableEsq1, OUTPUT);\n" +
        "  pinMode (EnableEsq2, OUTPUT);\n" +
        "  pinMode (VelMotEsq, OUTPUT);\n" +
        "  \n" +
        "  servomotor.attach(9); // Associar o servomotor ao pino em que esta conetado, neste caso o pino 9\n" +
        "  servomotor.write(posicao); // Colocar o servomotor na posicao inicial\n" +
        "  delay(1000);\n" +
        "  \n" +
        "}\n" +
        "\n" +
        "// Void Loop (programa em si, quais as ordens que o robo deve seguir)\n" +
        "\n" +
        "void loop ()\n" +
        "{//inicio do void loop\n" +
        texto +
        "\n\n" +
        "}//fim do void loop";
        
        area_resultado.setForeground(Color.black);
        area_resultado.setText(texto);
    }
    
    public void erro(String linha) {
        area_resultado.setForeground(Color.red);
        area_resultado.setText("ERRO\n" + linha);
    }

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try
        {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
            {
                if ("Nimbus".equals(info.getName()))
                {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (ClassNotFoundException ex)
        {
            java.util.logging.Logger.getLogger(Conversor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (InstantiationException ex)
        {
            java.util.logging.Logger.getLogger(Conversor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (IllegalAccessException ex)
        {
            java.util.logging.Logger.getLogger(Conversor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (javax.swing.UnsupportedLookAndFeelException ex)
        {
            java.util.logging.Logger.getLogger(Conversor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                new Conversor().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea area_fonte;
    private javax.swing.JTextArea area_resultado;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
